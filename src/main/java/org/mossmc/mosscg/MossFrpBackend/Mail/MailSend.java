package org.mossmc.mosscg.MossFrpBackend.Mail;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;
import static org.mossmc.mosscg.MossFrpBackend.Mail.MailMain.*;

public class MailSend {
    public static void asyncSendEmail(Enums.mailType type, String targetMail, JSONObject replacements) {
        Runnable runnable = () -> sendMail(type,targetMail,replacements);
        runnable.run();
    }

    public static void sendMail(Enums.mailType type, String targetMail, JSONObject replacements) {
        try {
            MimeMessage mimeMessage = MailTemplate.getMimeMessage(type,targetMail,replacements);
            sendInfo("发送了一封邮件："+targetMail+" | "+mimeMessage.getSubject()+" | "+mimeMessage.getContent());
            Transport mailTransport = mailSession.getTransport();
            mailTransport.connect(mailAccount, mailPassword);
            mailTransport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            mailTransport.close();
        } catch (Exception e) {
            LoggerSender.sendException(e);
        }
    }

    public static void sendCodeAll(String email,String info) {
        JSONObject replacements = new JSONObject();
        replacements.put("[info]",info.replace("\r\n","<br>"));
        sendMail(Enums.mailType.CODE,email,replacements);
    }

    public static void sendVerification(String email,String verification) {
        if (!MailCoolDown.checkCoolDown(email)) {
            return;
        }
        MailCoolDown.inputCoolDown(email);
        JSONObject replacements = new JSONObject();
        replacements.put("[code]",verification);
        sendMail(Enums.mailType.VERIFICATION,email,replacements);
    }

    public static void sendOneDay(String email,String node,String number) {
        JSONObject replacements = new JSONObject();
        replacements.put("[node]",node);
        replacements.put("[number]",number);
        sendMail(Enums.mailType.ONE,email,replacements);
    }

    public static void sendOutdated(String email,String node,String number) {
        JSONObject replacements = new JSONObject();
        replacements.put("[node]",node);
        replacements.put("[number]",number);
        sendMail(Enums.mailType.OUTDATED,email,replacements);
    }

    public static void sendRemoved(String email,String node,String number) {
        JSONObject replacements = new JSONObject();
        replacements.put("[node]",node);
        replacements.put("[number]",number);
        sendMail(Enums.mailType.REMOVED,email,replacements);
    }

    public static void sendBannedUser(String email,String reason) {
        JSONObject replacements = new JSONObject();
        replacements.put("[reason]",reason);
        sendMail(Enums.mailType.BANNED_USER,email,replacements);
    }

    public static void sendUnBannedUser(String email,String reason) {
        JSONObject replacements = new JSONObject();
        replacements.put("[reason]",reason);
        sendMail(Enums.mailType.UNBANNED_USER,email,replacements);
    }

    public static void sendBannedCode(String email,String node,String number,String reason) {
        JSONObject replacements = new JSONObject();
        replacements.put("[node]",node);
        replacements.put("[number]",number);
        replacements.put("[reason]",reason);
        sendMail(Enums.mailType.BANNED_CODE,email,replacements);
    }

    public static void sendUnBannedCode(String email,String node,String number,String reason) {
        JSONObject replacements = new JSONObject();
        replacements.put("[node]",node);
        replacements.put("[number]",number);
        replacements.put("[reason]",reason);
        sendMail(Enums.mailType.UNBANNED_CODE,email,replacements);
    }

    public static void sendADPass(String email,String ID,String day,String weight,String link) {
        JSONObject replacements = new JSONObject();
        replacements.put("[ID]",ID);
        replacements.put("[day]",day);
        replacements.put("[weight]",weight);
        replacements.put("[link]",link);
        sendMail(Enums.mailType.AD_PASS,email,replacements);
    }

    public static void sendADReject(String email,String ID,String reason) {
        JSONObject replacements = new JSONObject();
        replacements.put("[ID]",ID);
        replacements.put("[reason]",reason);
        sendMail(Enums.mailType.AD_REJECT,email,replacements);
    }

    public static void sendADOutdated(String email,String ID,String shown) {
        JSONObject replacements = new JSONObject();
        replacements.put("[ID]",ID);
        replacements.put("[shown]",shown);
        sendMail(Enums.mailType.AD_OUTDATED,email,replacements);
    }
}
