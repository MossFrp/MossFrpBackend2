package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenGet;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;

import java.io.IOException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class RequestLogin {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            String loginType = data.getString("loginType");
            String account = data.getString("account");
            String password = data.getString("password");
            String IP = data.getString("remoteIP");
            String encryption = data.getString("encryption");
            if (account == null||password == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            if (loginType==null) {
                loginType = "email";
            }
            switch (loginType) {
                case "qq":
                    if (!UserCache.qqCache.contains(account)) {
                        setFailedData(responseData,IP);
                        return;
                    }
                    break;
                case "userID":
                    if (UserCache.userCount+10001000 < Integer.parseInt(account)) {
                        setFailedData(responseData,IP);
                        return;
                    }
                    break;
                case "email":
                default:
                    account = account.toLowerCase();
                    if (!UserCache.emailCache.contains(account)) {
                        setFailedData(responseData,IP);
                        return;
                    }
                    break;
            }
            JSONObject userData = UserInfo.getUserInfo(loginType,account);
            if (userData == null) {
                setFailedData(responseData,IP);
                return;
            }
            String encryptedPassword;
            if (encryption != null && encryption.equals("true")) {
                encryptedPassword = EncryptMain.encode(password,true);
            } else {
                encryptedPassword = EncryptMain.encode(password);
            }
            if (!encryptedPassword.equals(userData.getString("password"))) {
                setFailedData(responseData,IP);
                return;
            }
            if (userData.getString("level").equals("banned")) {
                setBannedData(responseData,IP);
                return;
            }
            String remoteIP = data.getString("remoteIP");
            String token = TokenGet.registerToken(remoteIP, Enums.typeAPI.CLIENT,userData.getString("userID"));
            setSuccessData(responseData,token);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData, String token) {
        responseData.put("status","200");
        responseData.put("token",token);
    }

    public static void setFailedData(JSONObject responseData, String IP) {
        responseData.put("status","404");
        responseData.put("message","Unknown user or invalid password.");
        WebBlacklist.addBadRequestCount(IP,1);
    }

    public static void setBannedData(JSONObject responseData, String IP) {
        responseData.put("status","403");
        responseData.put("message","You was banned from MossFrp! Ask admin for more info!");
        WebBlacklist.addBadRequestCount(IP,1);
    }
}
