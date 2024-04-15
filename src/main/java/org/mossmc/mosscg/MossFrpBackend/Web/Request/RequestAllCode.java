package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

public class RequestAllCode {
    /**
     * 获得该节点全部穿透码JSON格式
     * {
     *     type: "allCode",
     *     node: "sq1",
     *     auth: "arhENaDnABbHUIhbRb651"
     * }
     **/
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        if (!TokenCheck.checkNode(data)) {
            RequestWrongArgument.getReply(exchange, data, responseData);
            return;
        }
        JSONObject dataCode = CodeInfo.getNodeCodeInfo(data.getString("node"));
        NodeCache.nodeUpdate.remove(data.getString("node"));
        if (dataCode == null) {
            responseData.put("status","500");
        } else {
            responseData.putAll(dataCode);
            responseData.put("status","200");
        }
    }

    public static JSONObject requestData;

    public static JSONObject getRequest() {
        if (requestData == null) {
            requestData = new JSONObject();
            requestData.put("type", "allCode");
            requestData.put("node", BasicInfo.getConfig("nodeName"));
            requestData.put("auth", BasicInfo.getConfig("nodeAuth"));
        }
        return requestData;
    }
}
