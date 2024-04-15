package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RequestUnknownType {
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        responseData.clear();
        responseData.put("status","400");
        responseData.put("message","Unknown request or not enough arguments.");
    }
}
