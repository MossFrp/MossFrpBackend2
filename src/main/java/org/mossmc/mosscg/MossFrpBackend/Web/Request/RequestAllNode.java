package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestAllNode {
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
            JSONObject nodeData = NodeCache.getLightNodeInfo();
            if (data.containsKey("getAsList") && data.getString("getAsList").equals("true")) {
                List<JSONObject> dataList = new ArrayList<>();
                NodeCache.nodeList.forEach(node -> {
                    dataList.add(nodeData.getJSONObject(node));
                });
                responseData.put("nodeData",dataList);
            } else {
                responseData.put("nodeData",nodeData);
                responseData.put("nodeList",NodeCache.nodeList);
            }
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");
    }
}
