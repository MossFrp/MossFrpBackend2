package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenGet;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;

import java.io.IOException;

public class RequestNoPermission {
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        responseData.clear();
        responseData.put("status","403");
        responseData.put("message","No permission to access that.");
        //TokenGet.removeToken(data.getString("token"));
        WebBlacklist.addBadRequestCount(data.getString("remoteIP"),1);
    }
}
