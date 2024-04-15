package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailMain;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;

import java.util.HashMap;
import java.util.Map;

public class WebVerification {
    public static Map<String, JSONObject> emailVerificationMap = new HashMap<>();

    //这里的type有以下的情况
    public static void sendVerificationMail(String email,String key) {
        String code = BasicInfo.getRandomString(6);
        JSONObject data = new JSONObject();
        data.put("key",key);
        data.put("code",code);
        emailVerificationMap.remove(email);
        emailVerificationMap.put(email,data);
        MailSend.sendVerification(email,code);
    }

    public static boolean verifyCode(String email,String key,String code) {
        if (!emailVerificationMap.containsKey(email)) {
            return false;
        }
        JSONObject data = emailVerificationMap.get(email);
        if (!data.getString("key").equals(key)) {
            return false;
        }
        if (!data.getString("code").equals(code.toUpperCase())) {
            return false;
        }
        emailVerificationMap.remove(email);
        return true;
    }
}
