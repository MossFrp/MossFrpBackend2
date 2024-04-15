package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoAPI;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class WebResponse {
    public static void initBasicResponse(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "*");
        headers.add("Access-Control-Max-Age", "864000");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "true");
    }

    public static void completeResponse(HttpExchange exchange, JSONObject responseData, JSONObject data, Enums.typeAPI typeAPI,long receive){
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(exchange.getResponseBody(), StandardCharsets.UTF_8);
        try {
            InfoAPI.usage(exchange, WebBasic.getRemoteIP(exchange), typeAPI, responseData,data);
            exchange.sendResponseHeaders(200,0);
            responseData.put("time",System.currentTimeMillis()-receive);
            oStreamWriter.append(responseData.toString());
        } catch (Exception e) {
            sendWarn("API类型为"+typeAPI.name()+"的请求在返回响应时发生错误，原因："+e.getMessage()+"，数据如下：["+data.toString()+"]["+responseData.toString()+"]");
        } finally {
            try {
                oStreamWriter.close();
                exchange.close();
            } catch (IOException e) {
                sendWarn("关闭API类型为"+typeAPI.name()+"的输出流时发生错误，原因："+e.getMessage());
            }
        }
    }
}
