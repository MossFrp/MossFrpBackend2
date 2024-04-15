package org.mossmc.mosscg.MossFrpBackend.Mail;

import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import javax.mail.Session;
import java.util.Properties;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;

public class MailMain {
    public static String mailAccount;
    public static String mailPassword;
    public static String mailSMTPHost;
    public static String mailSMTPPort;
    public static Session mailSession;

    public static void loadMail(){
        try {
            MailTemplate.initTemplate();
            mailAccount = getConfig("mailAccount");
            mailPassword = getConfig("mailPassword");
            mailSMTPHost = getConfig("mailSMTPHost");
            mailSMTPPort = getConfig("mailSMTPPort");
            String mailDebug = getConfig("mailDebug");
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.host", mailSMTPHost);
            props.setProperty("mail.smtp.auth", "true");
            //props.setProperty("mail.smtp.port", mailSMTPPort);
            mailSession = Session.getInstance(props);
            mailSession.setDebug(mailDebug.equals("true"));
        } catch (Exception e) {
            LoggerSender.sendException(e);
        }
    }
}
