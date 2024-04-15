package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.WebShop;

import java.io.IOException;

public class RequestShop {
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
            responseData.put("shopData",WebShop.shopData);
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");

    }
}
