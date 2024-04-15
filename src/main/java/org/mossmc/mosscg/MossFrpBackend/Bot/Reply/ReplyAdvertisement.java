package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.WebAdvertisement;

import java.util.List;

public class ReplyAdvertisement {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 300) {
            send.append("你没有权限！");
            return;
        }
        if (command.length < 2) {
            send.append("指令帮助：#广告 <新建/列表/审核/查询>");
            return;
        }
        switch (command[1]) {
            case "新建":
                if (command.length < 6) {
                    send.append("指令帮助：#广告 新建 <天数> <权重> <图片链接> <跳转链接>");
                    break;
                }
                int coin;
                try {
                    coin = Integer.parseInt(command[2])*Integer.parseInt(command[3])*10;
                } catch (Exception e) {
                    send.append("天数或权重参数错误！请检查");
                    break;
                }
                BotConfirm.inputConfirmMap(botType.name(),number,"task","advertisement-new");
                BotConfirm.inputConfirmMap(botType.name(),number,"day",command[2]);
                BotConfirm.inputConfirmMap(botType.name(),number,"weight",command[3]);
                BotConfirm.inputConfirmMap(botType.name(),number,"link",command[4]);
                BotConfirm.inputConfirmMap(botType.name(),number,"jump",command[5]);
                BotConfirm.inputConfirmMap(botType.name(),number,"coin", String.valueOf(coin));
                send.append("您的广告数据如下：\r\n");
                send.append("天数：").append(command[2]).append("\r\n");
                send.append("权重：").append(command[3]).append("\r\n");
                send.append("图片链接：").append(command[4]).append("\r\n");
                send.append("跳转链接：").append(command[5]).append("\r\n");
                send.append("所需货币：").append(coin).append("\r\n");
                send.append("请确认您的广告数据是否有误！确认无误请发送 #确认 来确认您的数据！");
                break;
            case "列表":
                send.append("MossFrp广告列表");
                List <JSONObject> ADList;
                if (command.length >= 3 && command[2].equals("all") && permission <= 100) {
                     ADList = WebAdvertisement.getAllAD();
                } else {
                     ADList = WebAdvertisement.getUserAD(userID);
                }
                if (ADList == null) {
                    send.append("\r\n您还没有投放广告喔~请使用 #广告 指令投放广告~");
                    break;
                }
                ADList.forEach(data -> send.append(WebAdvertisement.getADString(data,"light")));
                break;
            case "审核":
                if (permission > 100) {
                    send.append("你没有权限！");
                    break;
                }
                if (command.length < 3) {
                    send.append("指令帮助：#广告 审核 <列表/通过/拒绝>");
                    break;
                }
                switch (command[2]) {
                    case "列表":
                        send.append("当前需审核广告列表：\r\n");
                        List<JSONObject> data = WebAdvertisement.getCheckAD();
                        if (data == null) {
                            send.append("数据查询失败！");
                            break;
                        }
                        final boolean[] first = {true};
                        data.forEach(ADData -> {
                            if (!first[0]) {
                                send.append("、");
                            }
                            first[0] = false;
                            send.append(ADData.getString("ID"));
                        });
                        break;
                    case "通过":
                        if (command.length == 3) {
                            send.append("指令帮助：#广告 审核 通过 <ID>");
                            break;
                        }
                        WebAdvertisement.checkPass(Integer.parseInt(command[3]));
                        send.append("已通过ID为").append(command[3]).append("的广告！");
                        break;
                    case "拒绝":
                        if (command.length <= 4) {
                            send.append("指令帮助：#广告 审核 拒绝 <ID> <原因>");
                            break;
                        }
                        WebAdvertisement.checkReject(Integer.parseInt(command[3]),command[4]);
                        send.append("已拒绝ID为").append(command[3]).append("的广告！");
                        break;
                    default:
                        send.append("指令帮助：#广告 审核 <列表/通过/拒绝>");
                        break;
                }
                break;
            case "查询":
                if (permission > 100) {
                    send.append("你没有权限！");
                    break;
                }
                if (command.length <= 3) {
                    send.append("指令帮助：#广告 查询 <key> <value>");
                    break;
                }
                JSONObject data = WebAdvertisement.getADData(command[2],command[3]);
                if (data == null) {
                    send.append("未查询到数据！请检查后重试！");
                    break;
                }
                send.append("MossFrp广告数据");
                send.append(WebAdvertisement.getADString(data,"full"));
                break;
            default:
                send.append("指令帮助：#广告 <新建/列表/审核/查询>");
                break;
        }
    }
}
