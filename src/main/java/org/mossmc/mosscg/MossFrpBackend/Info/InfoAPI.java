package org.mossmc.mosscg.MossFrpBackend.Info;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Enums;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class InfoAPI {
    public static void usage(HttpExchange exchange, String remoteIP, Enums.typeAPI type, JSONObject responseData,JSONObject data) {
        sendAPI("["+type.name()+"]["+remoteIP+"]["+exchange.getRequestURI().toString()+"]["+data.toString()+"]["+responseData.toString()+"]["+exchange.getRequestHeaders().entrySet()+"]",type);
    }
}
