package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.User.*;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenGet;
import org.mossmc.mosscg.MossFrpBackend.Web.WebVerification;

import java.io.IOException;

public class RequestInfoUpdate {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            if (!TokenCheck.checkClient(data.getString("token"),data.getString("remoteIP"))) {
                RequestWrongToken.getReply(exchange, data, responseData);
                return;
            }
            int permission = UserPermission.getPermission(TokenCache.tokenUserMap.get(data.getString("token")));
            if (permission > 600) {
                RequestNoPermission.getReply(exchange, data, responseData);
                return;
            }
            String userID = TokenCache.tokenUserMap.get(data.getString("token"));
            String key = data.getString("key");
            String value = data.getString("value");
            String code = data.getString("code");
            String code2 = data.getString("code2");
            if (userID == null ||key == null||code == null||value == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            String email = UserInfo.getInfo("userID",userID,"email");
            if (!WebVerification.verifyCode(email,"infoUpdate",code)) {
                setWrongCodeData(responseData);
                return;
            }
            String check;
            switch (key) {
                case "username":
                    check = UserInfoCheck.checkName(value);
                    if (!check.equals("pass")) {
                        setWrongInfoData(responseData,check);
                        return;
                    }
                    break;
                case "password":
                    check = UserInfoCheck.checkPassword(value);
                    if (!check.equals("pass")) {
                        setWrongInfoData(responseData,check);
                        return;
                    }
                    value = EncryptMain.encode(value);
                    break;
                case "email":
                    if (code2 == null) {
                        RequestWrongArgument.getReply(exchange, data, responseData);
                        return;
                    }
                    if (!WebVerification.verifyCode(value,"infoUpdate",code2)) {
                        setWrongCodeData(responseData);
                        return;
                    }
                    check = UserInfoCheck.checkEmail(value);
                    if (!check.equals("pass")) {
                        setWrongInfoData(responseData,check);
                        return;
                    }
                    check = UserInfoCheck.checkEmailRegister(value);
                    if (!check.equals("pass")) {
                        setWrongInfoData(responseData,check);
                        return;
                    }
                    break;
                default:
                    RequestWrongArgument.getReply(exchange, data, responseData);
                    return;
            }
            UserInfo.updateInfo("userID",userID,key,value);
            switch (key) {
                case "username":
                    UserCache.nameCache.add(value);
                    break;
                case "email":
                    UserCache.emailCache.add(value);
                    break;
                default:
                    break;
            }
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
