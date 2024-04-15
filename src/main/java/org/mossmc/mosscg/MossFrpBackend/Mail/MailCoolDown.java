package org.mossmc.mosscg.MossFrpBackend.Mail;

import java.util.HashMap;
import java.util.Map;

public class MailCoolDown {
    public static Map<String,Long> mailCoolDownMap = new HashMap<>();
    public static int coolDownTime = 60;

    public static void inputCoolDown(String mail) {
        mailCoolDownMap.put(mail,System.currentTimeMillis());
    }

    //返回true则无冷却，返回false就是有冷却
    public static boolean checkCoolDown(String mail) {
        if (!mailCoolDownMap.containsKey(mail)) {
            return true;
        }
        return mailCoolDownMap.get(mail) + coolDownTime * 1000L <= System.currentTimeMillis();
    }
}
