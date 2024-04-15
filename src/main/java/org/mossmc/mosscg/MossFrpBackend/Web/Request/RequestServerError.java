package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RequestServerError {
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        responseData.clear();
        responseData.put("status","500");
        responseData.put("message","Server error!");
    }
}
