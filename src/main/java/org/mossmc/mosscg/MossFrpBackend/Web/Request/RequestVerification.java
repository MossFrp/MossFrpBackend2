package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.WebVerification;

import java.io.IOException;

public class RequestVerification {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            String email = data.getString("email");
            String key = data.getString("key");
            if (email == null || key == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            String check = UserInfoCheck.checkEmail(email);
            if (!check.equals("pass")) {
                setWrongInfoData(responseData,check);
                return;
            }
            WebVerification.sendVerificationMail(email,key);
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");
    }

    public static void setWrongInfoData(JSONObject responseData,String message) {
        responseData.put("status","403");
        responseData.put("message",message);
    }
}
