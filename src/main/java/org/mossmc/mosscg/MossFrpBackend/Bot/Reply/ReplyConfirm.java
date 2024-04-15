package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.*;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeAdd;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeRemain;
import org.mossmc.mosscg.MossFrpBackend.User.*;
import org.mossmc.mosscg.MossFrpBackend.Web.WebAdvertisement;

import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm.removeConfirmMap;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class ReplyConfirm {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 999) {
            send.append("你没有权限！");
            return;
        }
        boolean noRemove = false;
        if (!BotConfirm.containConfirmMap(botType.name(),number)) {
            send.append("确认失败！你没有要确认的操作！");
            return;
        }
        try {
            Map<String,String> confirmCache = BotConfirm.getConfirmMap(botType.name(),number);
            String type = confirmCache.get("task");
            if (type.equals("code-remove")) {
                String node = confirmCache.get("node");
                String frpNumber = confirmCache.get("number");
                JSONObject codeInfo = CodeInfo.getCodeInfoByNodeNumber(node,frpNumber);
                if (codeInfo == null) {
                    send.append("无法查询到数据！请确认参数或重试！");
                    return;
                }
                int band = codeInfo.getInteger("band");
                int dateRemain = TimeRemain.dateRemainDay(node,frpNumber);
                String allowCheck = CodeInfoCheck.checkRemove(node,frpNumber,botType.name(),number);
                if (!allowCheck.equals("pass")) {
                    send.append(allowCheck);
                    return;
                }
                int price;
                int returnCoin;
                try {
                    price = Integer.parseInt(NodeCache.nodeCache.get(node).get("price"));
                    returnCoin = (dateRemain-1)*price*band;
                    if (returnCoin > 0) {
                        UserCoinAdd.coinAdd(Enums.coinType.SILVER, returnCoin, number, botType.name());
                    } else {
                        returnCoin = 0;
                    }
                } catch (Exception e) {
                    sendException(e);
                    send.append("删除成功！返回银币失败！请联系管理员！");
                    return;
                }
                CodeRemove.remove(codeInfo);
                send.append("已确认你的信息！\r\n已返回银币：").append(returnCoin).append("\r\n【删除成功！】");
                return;
            }
            if (type.equals("code-refresh")) {
                String frpNumber = confirmCache.get("number");
                String node = confirmCache.get("node");
                JSONObject codeInfo = CodeInfo.getCodeInfoByNodeNumber(node,frpNumber);
                if (codeInfo == null) {
                    send.append("未知的穿透码！请检查节点/编号是否输入错误！");
                    return;
                }
                if (!codeInfo.getString("user").equals(userID) && permission > 0) {
                    send.append("这不是你的穿透码，无法刷新！");
                    return;
                }
                String coinCheck = UserCoinTake.coinTake(Enums.coinType.MIXED,200,number,botType.name(),null);
                if (!coinCheck.equals("pass")) {
                    send.append(coinCheck);
                    return;
                }

                CodeRefresh.refresh(codeInfo);
                send.append("已确认你的信息！\r\n【刷新成功！】");
                return;
            }
            if (type.equals("code-new")) {
                String band = confirmCache.get("band");
                String node = confirmCache.get("node");
                String date = confirmCache.get("date");
                String allowCheck = CodeInfoCheck.checkCreate(node,band,date,userID,permission);
                if (!allowCheck.equals("pass")) {
                    send.append(allowCheck);
                    return;
                }
                int needCoin = Integer.parseInt(band)*Integer.parseInt(date)*Integer.parseInt(NodeCache.nodeCache.get(node).get("price"));
                Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(node).get("coin").toUpperCase());
                String coinCheck = UserCoinTake.coinTake(coinType,needCoin,number,botType.name(),NodeCache.nodeCache.get(node).get("provider"));
                if (!coinCheck.equals("pass")) {
                    send.append(coinCheck);
                    return;
                }
                assert userID != null;
                CodeCreate.create(node,userID,Integer.parseInt(band),Integer.parseInt(date));
                send.append("已确认你的信息！\r\n【创建成功！】");
                return;
            }
            if (type.equals("code-date")) {
                String date = confirmCache.get("date");
                String node = confirmCache.get("node");
                String frpNumber = confirmCache.get("number");
                String coinNeed = confirmCache.get("coin");
                Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(node).get("coin").toUpperCase());
                String allowCheck = CodeInfoCheck.checkDate(node,frpNumber,botType.name(),number,date);
                if (!allowCheck.equals("pass")) {
                    send.append(allowCheck);
                    return;
                }
                String coinCheck = UserCoinTake.coinTake(coinType,Integer.parseInt(coinNeed),number,botType.name(),NodeCache.nodeCache.get(node).get("provider"));
                if (!coinCheck.equals("pass")) {
                    send.append(coinCheck);
                    return;
                }
                String dateCheck = TimeAdd.dateAddDay(Integer.parseInt(date),node,frpNumber);
                if (dateCheck.equals("success")) {
                    send.append("已确认你的信息！\r\n【续期成功！】");
                } else {
                    send.append("信息已确认，续期失败！请联系管理员！");
                }
                return;
            }
            if (type.equals("code-band")) {
                String band = confirmCache.get("band");
                String node = confirmCache.get("node");
                String frpNumber = confirmCache.get("number");
                String coinNeed = confirmCache.get("coin");
                Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(node).get("coin").toUpperCase());
                String allowCheck = CodeInfoCheck.checkBand(node,frpNumber,botType.name(),number,band);
                if (!allowCheck.equals("pass")) {
                    send.append(allowCheck);
                    return;
                }
                String coinCheck = UserCoinTake.coinTake(coinType,Integer.parseInt(coinNeed),number,botType.name(),NodeCache.nodeCache.get(node).get("provider"));
                if (!coinCheck.equals("pass")) {
                    send.append(coinCheck);
                    return;
                }
                JSONObject data = CodeInfo.getCodeInfoByNodeNumber(node,frpNumber);
                if (data == null) {
                    send.append("信息已确认，升配失败！请联系管理员！");
                    return;
                }
                String bandString = data.getString("band");
                int bandOld = Integer.parseInt(bandString);
                int bandNew = bandOld + Integer.parseInt(band);
                CodeInfo.updateCodeInfo("ID",data.getString("ID"),"band",String.valueOf(bandNew));
                if (NodeCache.nodeCache.get(node).get("activity").equals("true")) {
                    CodeInfo.updateCodeInfo("ID",data.getString("ID"),"activity","true");
                }
                send.append("已确认你的信息！\r\n【升配成功！】");
                return;
            }
            if (type.equals("register")) {
                String email = confirmCache.get("email");
                String name = confirmCache.get("name");
                String password = confirmCache.get("password");
                String registerType = confirmCache.get("type");
                String allowCheck = UserInfoCheck.checkRegister(email,password,name,number,registerType);
                if (!allowCheck.equals("pass")) {
                    send.append(allowCheck);
                    return;
                }
                if (registerType.equals("qq")) {
                    String exceptEMail = number + "@qq.com";
                    if (email.equals(exceptEMail)) {
                        JSONObject data = new JSONObject();
                        data.put("qq",number);
                        UserCreate.createUser(name,email,password,data,"QQBot");
                        UserPermission.permissionMap.remove(userID);
                        send.append("已确认你的信息！检测到为对应QQ邮箱，已跳过验证码！\r\n【注册成功！】");
                        send.append("\r\nTips：赠送的3500银币已到账，快跟着docs.mossfrp.top上面的教程开始使用吧！");
                        return;
                    }
                }
                String verificationCode = BasicInfo.getRandomString(6);
                noRemove = true;
                BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-register");
                BotConfirm.inputConfirmMap(botType.name(),number,"code",verificationCode);
                MailSend.sendVerification(email,verificationCode);
                send.append("已确认你的信息！已发送邮箱验证码，请使用#确认 <验证码> 来验证你的邮箱！");
                return;
            }
            if (type.equals("update-user")) {
                String verificationCode = BasicInfo.getRandomString(6);
                String updateType = confirmCache.get("type");
                String email = UserInfo.getInfo(botType.name(),number,"email");
                if (updateType.equals("name")) {
                    noRemove = true;
                    BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-update-user-name");
                    BotConfirm.inputConfirmMap(botType.name(),number,"code",verificationCode);
                    MailSend.sendVerification(email,verificationCode);
                    send.append("已确认你的信息！已发送邮箱验证码，请使用#确认 <验证码> 来验证你的邮箱！");
                    return;
                }
                if (updateType.equals("email")) {
                    noRemove = true;
                    BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-update-user-email-old");
                    BotConfirm.inputConfirmMap(botType.name(),number,"code",verificationCode);
                    MailSend.sendVerification(email,verificationCode);
                    send.append("已确认你的信息！已向旧邮箱发送邮箱验证码，请使用#确认 <验证码> 来验证你的邮箱！");
                    return;
                }
                if (updateType.equals("password")) {
                    noRemove = true;
                    BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-update-user-password");
                    BotConfirm.inputConfirmMap(botType.name(),number,"code",verificationCode);
                    MailSend.sendVerification(email,verificationCode);
                    send.append("已确认你的信息！已发送邮箱验证码，请使用#确认 <验证码> 来验证你的邮箱！");
                    return;
                }
            }
            if (type.equals("verification-update-user-name")) {
                String verificationCode = confirmCache.get("code");
                if (command.length <= 1) {
                    noRemove = true;
                    send.append("请使用指令 #确认 确认验证码！\r\n示例：#确认 123456");
                    return;
                }
                if (command[1].equals(verificationCode)) {
                    String returnInfo = UserCoinTake.coinTake(Enums.coinType.MIXED,100,number,botType.name(),null);
                    if (!returnInfo.equals("pass")) {
                        send.append(returnInfo);
                        return;
                    }
                    UserInfo.updateInfo(botType.name(),number,"username",confirmCache.get("info"));
                    send.append("验证码正确！更新成功！");
                } else {
                    send.append("验证码错误！");
                }
                return;
            }
            if (type.equals("verification-update-user-password")) {
                String verificationCode = confirmCache.get("code");
                if (command.length <= 1) {
                    noRemove = true;
                    send.append("请使用指令 #确认 确认验证码！\r\n示例：#确认 123456");
                    return;
                }
                if (command[1].equals(verificationCode)) {
                    String returnInfo = UserCoinTake.coinTake(Enums.coinType.MIXED,50,number,botType.name(),null);
                    if (!returnInfo.equals("pass")) {
                        send.append(returnInfo);
                        return;
                    }
                    String password = EncryptMain.encode(confirmCache.get("info"));
                    UserInfo.updateInfo(botType.name(),number,"password",password);
                    send.append("验证码正确！更新成功！");
                } else {
                    send.append("验证码错误！");
                }
                return;
            }
            if (type.equals("verification-update-user-email-old")) {
                String verificationCode = confirmCache.get("code");
                if (command.length <= 1) {
                    noRemove = true;
                    send.append("请使用指令 #确认 确认验证码！\r\n示例：#确认 123456");
                    return;
                }
                if (command[1].equals(verificationCode)) {
                    noRemove = true;
                    String verificationCodeNew = BasicInfo.getRandomString(6);
                    BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-update-user-email-new");
                    BotConfirm.updateConfirmMap(botType.name(),number,"code",verificationCodeNew);
                    MailSend.sendVerification(confirmCache.get("info"),verificationCodeNew);
                    send.append("验证码正确！已向新邮箱发送邮箱验证码，请使用#确认 <验证码> 来验证你的邮箱！");
                } else {
                    send.append("验证码错误！");
                }
                return;
            }
            if (type.equals("verification-update-user-email-new")) {
                String verificationCode = confirmCache.get("code");
                if (command.length <= 1) {
                    noRemove = true;
                    send.append("请使用指令 #确认 确认验证码！\r\n示例：#确认 123456");
                    return;
                }
                if (command[1].equals(verificationCode)) {
                    String returnInfo = UserCoinTake.coinTake(Enums.coinType.MIXED,200,number,botType.name(),null);
                    if (!returnInfo.equals("pass")) {
                        send.append(returnInfo);
                        return;
                    }
                    UserInfo.updateInfo(botType.name(),number,"email",confirmCache.get("info"));
                    send.append("验证码正确！更新成功！");
                } else {
                    send.append("验证码错误！");
                }
                return;
            }
            if (type.equals("verification-register")) {
                if (command.length > 1 && command[1].equals(confirmCache.get("code"))) {
                    String email = confirmCache.get("email");
                    String name = confirmCache.get("name");
                    String password = confirmCache.get("password");
                    JSONObject data = new JSONObject();
                    data.put("qq",number);
                    UserCreate.createUser(name,email,password,data,"QQBot");
                    send.append("验证码正确！注册成功！");
                    send.append("\r\nTips：赠送的3500银币已到账，快跟着docs.mossfrp.top上面的教程开始使用吧！");
                    return;
                }
                send.append("验证码错误！");
                return;
            }
            if (type.equals("bind-email")) {
                String email = confirmCache.get("email");
                JSONObject userInfo = UserInfo.getUserInfo("email",email);
                if (userInfo == null) {
                    send.append("此邮箱未被注册！请使用 #注册 指令注册或在网页中注册！");
                    return;
                }
                if (userInfo.getString("qq") != null) {
                    send.append("此邮箱已绑定QQ！无法再次绑定！");
                    return;
                }
                String verificationCode = BasicInfo.getRandomString(6);
                noRemove = true;
                BotConfirm.updateConfirmMap(botType.name(),number,"task","verification-bind-email");
                BotConfirm.inputConfirmMap(botType.name(),number,"code",verificationCode);
                MailSend.sendVerification(email,verificationCode);
                send.append("已发送验证码！请使用 #确认 <验证码> 指令确认绑定信息！");
                return;
            }
            if (type.equals("verification-bind-email")) {
                if (command.length > 1 && command[1].equals(confirmCache.get("code"))) {
                    String bindType = confirmCache.get("type");
                    String bindNumber = confirmCache.get(bindType);
                    String email = confirmCache.get("email");
                    UserInfo.updateInfo("email",email,bindType,bindNumber);
                    UserCache.qqCache.add(bindNumber);
                    send.append("验证码正确！绑定成功！");
                    return;
                }
                send.append("验证码错误！");
                return;
            }
            if (type.equals("advertisement-new")) {
                int weight = Integer.parseInt(confirmCache.get("weight"));
                int day = Integer.parseInt(confirmCache.get("day"));
                int coin = Integer.parseInt(confirmCache.get("coin"));
                String link = confirmCache.get("link");
                String jump = confirmCache.get("jump");
                String coinCheck = UserCoinTake.coinTake(Enums.coinType.MIXED,coin,number,botType.name(),"10000000");
                if (!coinCheck.equals("pass")) {
                    send.append(coinCheck);
                    return;
                }
                assert userID != null;
                WebAdvertisement.addAD(day,weight,userID,link,jump);
                send.append("已确认您的信息！\r\n广告已提交审核，请耐心等待审核通过！");
                return;
            }
            send.append("未知的确认参数！请反馈给管理员！").append(confirmCache);
        } catch (Exception e) {
            sendException(e);
        } finally {
            if (!noRemove) {
                removeConfirmMap(botType.name(),number);
            }
        }
    }
}
