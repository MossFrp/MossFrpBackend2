package org.mossmc.mosscg.MossFrpBackend.Web.Handler;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.Request.*;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBasic;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;
import org.mossmc.mosscg.MossFrpBackend.Web.WebResponse;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class HandlerNode implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            long receive = System.currentTimeMillis();
            WebResponse.initBasicResponse(exchange);
            JSONObject data = WebBasic.loadRequestData(exchange);
            JSONObject responseData = new JSONObject();
            data.put("remoteIP",WebBasic.getRemoteIP(exchange));
            /*
            if (WebBlacklist.checkBlacklist(data.getString("remoteIP"))) {
                RequestBadData.getReply(exchange,data,responseData);
                WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.CLIENT);
                return;
            }
             */
            if (!InfoCheck.checkData(data)) {
                RequestBadData.getReply(exchange,data,responseData);
                WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.NODE,receive);
                return;
            }
            switch (data.getOrDefault("type","null").toString()) {
                case "allCode":
                    RequestAllCode.getReply(exchange,data,responseData);
                    break;
                case "heartbeat":
                    RequestHeartbeat.getReply(exchange,data,responseData);
                    break;
                case "nodeData":
                    RequestNodeData.getReply(exchange,data,responseData);
                    break;
                default:
                    RequestUnknownType.getReply(exchange,data,responseData);
                    break;
            }
            WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.NODE,receive);
        } catch (Exception e) {
            sendException(e);
        }
    }
}
