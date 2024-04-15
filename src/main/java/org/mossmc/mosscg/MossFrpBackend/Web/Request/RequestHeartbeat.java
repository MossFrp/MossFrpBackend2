package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeProcess;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeStatic;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

public class RequestHeartbeat {
    /**
     * 心跳包JSON格式
     * {
     *     type: "heartbeat",
     *     node: "sq1",
     *     auth: "arhENaDnABbHUIhbRb651"
     * }
     **/
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        if (!TokenCheck.checkNode(data)) {
            RequestWrongArgument.getReply(exchange, data, responseData);
            return;
        }
        String node = data.getString("node");
        NodeCache.nodeStatusCache.put(node, Enums.nodeStatusType.ONLINE);
        NodeCache.nodeVersionCache.put(node, data.getString("version"));
        responseData.put("update",String.valueOf(NodeCache.nodeUpdate.contains(node)));
        responseData.put("status","200");
        responseData.put("message","Receive.");
    }

    public static JSONObject requestData;

    public static JSONObject getRequest() {
        if (requestData == null) {
            requestData = new JSONObject();
            requestData.put("type", "heartbeat");
            requestData.put("node", BasicInfo.getConfig("nodeName"));
            requestData.put("auth", BasicInfo.getConfig("nodeAuth"));
            requestData.put("version", BasicInfo.getVersion);
        }
        return requestData;
    }
}
