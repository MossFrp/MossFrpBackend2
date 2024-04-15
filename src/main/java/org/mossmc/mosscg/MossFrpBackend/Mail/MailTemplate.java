package org.mossmc.mosscg.MossFrpBackend.Mail;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Mail.MailMain.mailSession;

public class MailTemplate {
    public static Map<Enums.mailType, JSONObject> templateMap = new HashMap<>();

    public static void initTemplate() {
        JSONObject templateVerification = new JSONObject();
        templateVerification.put("title", "MossFrp邮件验证码");
        templateVerification.put("message", "MossFrp 验证码<br>" +
                "验证码：【[code]】<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.VERIFICATION, templateVerification);

        JSONObject templateOneDay = new JSONObject();
        templateOneDay.put("title", "MossFrp临期提醒");
        templateOneDay.put("message", "MossFrp 穿透码临期提醒<br>" +
                "您的隧道穿透码即将在24小时之后到期！<br>" +
                "隧道穿透码信息：<br>" +
                "节点：[node]<br>" +
                "编号：[number]<br>" +
                "Tips：若未及时续期，穿透码将在到期后被关闭！<br>" +
                "届时端口将被空置，可能会被抢占哦~<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.ONE, templateOneDay);

        JSONObject templateOutdated = new JSONObject();
        templateOutdated.put("title", "MossFrp到期提醒");
        templateOutdated.put("message", "MossFrp 穿透码到期提醒<br>" +
                "您的隧道穿透码已在刚刚到期！<br>" +
                "隧道穿透码信息：<br>" +
                "节点：[node]<br>" +
                "编号：[number]<br>" +
                "Tips：若未及时续期，穿透码将在七天后永久删除！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.OUTDATED, templateOutdated);

        JSONObject templateRemoved = new JSONObject();
        templateRemoved.put("title", "MossFrp释放提醒");
        templateRemoved.put("message", "MossFrp 穿透码释放提醒<br>" +
                "您的隧道穿透码已在刚刚被释放！<br>" +
                "隧道穿透码信息：<br>" +
                "节点：[node]<br>" +
                "编号：[number]<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.REMOVED, templateRemoved);

        JSONObject templateCode = new JSONObject();
        templateCode.put("title", "MossFrp穿透码列表");
        templateCode.put("message", "[info]<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.CODE, templateCode);

        JSONObject templateBannedUser = new JSONObject();
        templateBannedUser.put("title", "MossFrp封禁提示");
        templateBannedUser.put("message", "MossFrp 封禁提示<br>" +
                "您的账户在刚刚被管理员封禁！<br>" +
                "原因：[reason]<br>" +
                "若有疑问，请发送相应邮件至1292141077@qq.com或在QQ交流群私信群主或管理获取帮助！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.BANNED_USER, templateBannedUser);

        JSONObject templateUnBannedUser = new JSONObject();
        templateUnBannedUser.put("title", "MossFrp解封提示");
        templateUnBannedUser.put("message", "MossFrp 解封提示<br>" +
                "您的账户在刚刚被管理员解封！<br>" +
                "原因：[reason]<br>" +
                "感谢您对MossFrp的支持！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.UNBANNED_USER, templateUnBannedUser);

        JSONObject templateBannedCode = new JSONObject();
        templateBannedCode.put("title", "MossFrp封禁提示");
        templateBannedCode.put("message", "MossFrp 封禁提示<br>" +
                "您的穿透码在刚刚被管理员封禁！<br>" +
                "原因：[reason]<br>" +
                "隧道穿透码信息：<br>" +
                "节点：[node]<br>" +
                "编号：[number]<br>" +
                "若有疑问，请发送相应邮件至1292141077@qq.com或在QQ交流群私信群主或管理获取帮助！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.BANNED_CODE, templateBannedCode);

        JSONObject templateUnBannedCode = new JSONObject();
        templateUnBannedCode.put("title", "MossFrp解封提示");
        templateUnBannedCode.put("message", "MossFrp 解封提示<br>" +
                "您的穿透码在刚刚被管理员解封！<br>" +
                "原因：[reason]<br>" +
                "隧道穿透码信息：<br>" +
                "节点：[node]<br>" +
                "编号：[number]<br>" +
                "感谢您对MossFrp的支持！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.UNBANNED_CODE, templateUnBannedCode);

        JSONObject templateADPass = new JSONObject();
        templateADPass.put("title", "MossFrp审核提示");
        templateADPass.put("message", "MossFrp 审核提示<br>" +
                "您投放的广告已在刚刚通过审核！<br>" +
                "ID：[ID]<br>" +
                "时长：[day]天<br>" +
                "权重：[weight]<br>" +
                "链接：[link]<br>" +
                "感谢您对MossFrp的支持！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.AD_PASS, templateADPass);

        JSONObject templateADReject = new JSONObject();
        templateADReject.put("title", "MossFrp审核提示");
        templateADReject.put("message", "MossFrp 审核提示<br>" +
                "您投放的广告未通过审核，已被冻结！<br>" +
                "ID：[ID]<br>" +
                "原因：[reason]<br>" +
                "被冻结的广告将不返还货币！<br>" +
                "您可以联系管理员进行复审等操作！<br>" +
                "感谢您对MossFrp的支持！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.AD_REJECT, templateADReject);

        JSONObject templateADOutdated = new JSONObject();
        templateADOutdated.put("title", "MossFrp提示");
        templateADOutdated.put("message", "MossFrp 提示<br>" +
                "您投放的广告已到期下架~<br>" +
                "ID：[ID]<br>" +
                "累计展现：[shown]<br>" +
                "感谢您对MossFrp的支持！<br>" +
                messageFoot
        );
        templateMap.put(Enums.mailType.AD_OUTDATED, templateADOutdated);
    }

    public static MimeMessage getMimeMessage(Enums.mailType type, String receiveMail, JSONObject info) {
        MimeMessage message = new MimeMessage(mailSession);
        try {
            JSONObject messageInfo = templateMap.get(type);
            final String[] sendMessage = {messageInfo.getString("message")};
            info.forEach((key, value) -> sendMessage[0] = sendMessage[0].replace(key, value.toString()));
            message.setFrom(new InternetAddress(MailMain.mailAccount, "MossFrp", "UTF-8"));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, receiveMail, "UTF-8"));
            message.setSubject(messageInfo.getString("title"), "UTF-8");
            message.setContent(sendMessage[0], "text/html;charset=UTF-8");
            message.setSentDate(new Date());
            message.saveChanges();
        } catch (Exception e) {
            LoggerSender.sendException(e);
        }
        return message;
    }

    public static String messageFoot =
            "----------=== MossFrp ===----------<br>" +
                    "欢迎使用MossFrp~！<br>" +
                    "一个易用、轻量、高性能的Frp内网穿透服务~<br>" +
                    "官网：www.mossfrp.top";
    public static String oldMessageFoot =
            "----------=== MossFrp ===----------<br>" +
                    "欢迎使用MossFrp！<br>" +
                    "一个易用、轻量、高性能的Frp内网穿透服务~<br>" +
                    "----------=== MossFrp ===----------<br>" +
                    "B站官方：@墨守MossCG<br>" +
                    "QQ交流群：1072507973<br>" +
                    "官网：www.mossfrp.top";
}
