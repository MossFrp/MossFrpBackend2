package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestStatistic {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            if (!data.containsKey("token") || data.getString("token").equals("undefined")) {
                responseData.put("userCount", UserCache.userCount);
                responseData.put("codeCount", UserCache.codeCount);
                responseData.put("nodeCount", UserCache.nodeCount);
                responseData.put("signInCount", UserSignIn.signIn.size());
                setSuccessData(responseData);
                return;
            }
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
            responseData.put("userCount", UserCache.userCount);
            responseData.put("codeCount", UserCache.codeCount);
            responseData.put("nodeCount", UserCache.nodeCount);
            responseData.put("signInCount", UserSignIn.signIn.size());
            responseData.put("userCodeCount", UserCache.getUserCodeCount(userID));
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");

    }
}
