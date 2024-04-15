package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.User.UserCreate;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.WebVerification;

import java.io.IOException;

public class RequestRegister {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            String email = data.getString("email");
            String username = data.getString("username");
            String code = data.getString("code");
            String password = data.getString("password");
            if (email == null ||username == null||code == null||password == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            if (!WebVerification.verifyCode(email,"register",code)) {
                setWrongCodeData(responseData);
                return;
            }
            String check = UserInfoCheck.checkRegister(email,password,username,null,"web");
            if (!check.equals("pass")) {
                setWrongInfoData(responseData,check);
                return;
            }
            UserCreate.createUser(username,email.toLowerCase(),password,null,"WebAPI");
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
