package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.User.UserLuck;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

public class RequestSignIn {
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
            int luck = UserLuck.getLuck(Long.parseLong(userID));
            String luckMessage = UserLuck.getLuckMessage(luck);
            responseData.put("luck",luck);
            responseData.put("luckMessage",luckMessage);

            boolean first = !UserSignIn.getUserHaveGot(Long.parseLong(userID));
            String signInMessage = UserSignIn.getSignIn(Long.parseLong(userID));
            int coin = UserSignIn.getCoin(Long.parseLong(userID));
            responseData.put("first",first);
            responseData.put("signInMessage",signInMessage);
            responseData.put("coin",coin);
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");
    }
}
