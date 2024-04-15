package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;

import java.io.IOException;

public class RequestWrongArgument {
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        responseData.clear();
        responseData.put("status","400");
        responseData.put("message","Argument wrong or missed.");
        WebBlacklist.addBadRequestCount(data.getString("remoteIP"),1);
    }
}
