package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.User.*;
import org.mossmc.mosscg.MossFrpBackend.Web.WebVerification;

import java.io.IOException;

public class RequestForgetPassword {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            String email = data.getString("email");
            String code = data.getString("code");
            String password = data.getString("password");
            if (email == null||code == null||password == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            if (!WebVerification.verifyCode(email,"forgetPassword",code)) {
                setWrongCodeData(responseData);
                return;
            }
            if (!UserCache.emailCache.contains(email.toLowerCase())) {
                setWrongInfoData(responseData,"Email not registered yet");
                return;
            }
            String check = UserInfoCheck.checkPassword(password);
            if (!check.equals("pass")) {
                setWrongInfoData(responseData,check);
                return;
            }
            UserInfo.updateInfo("email",email,"password", EncryptMain.encode(password));
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");
    }

    public static void setWrongCodeData(JSONObject responseData) {
        responseData.put("status","404");
    }

    public static void setWrongInfoData(JSONObject responseData,String reason) {
        responseData.put("status","403");
        responseData.put("message",reason);
    }
}
