package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeStatic;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class RequestNodeData {
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
        NodeStatic.updateData(node,"CPUUsage",data.getString("CPUUsage"));
        NodeStatic.updateData(node,"MemoryUsed",data.getString("MemoryUsed"));
        NodeStatic.updateData(node,"MemoryTotal",data.getString("MemoryTotal"));
        NodeStatic.updateData(node,"DownloadBand",data.getString("DownloadBand"));
        NodeStatic.updateData(node,"UploadBand",data.getString("UploadBand"));
        NodeStatic.updateData(node,"DownloadTotal",data.getString("DownloadTotal"));
        NodeStatic.updateData(node,"UploadTotal",data.getString("UploadTotal"));
        responseData.put("status","200");
        responseData.put("message","Receive.");
    }

    public static JSONObject getRequest() {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "nodeData");
        requestData.put("node", BasicInfo.getConfig("nodeName"));
        requestData.put("auth", BasicInfo.getConfig("nodeAuth"));
        requestData.put("CPUUsage",InfoSystem.getCPUUsage.replace("%",""));
        requestData.put("MemoryUsed",InfoSystem.getMemoryUsed);
        requestData.put("MemoryTotal",InfoSystem.getMemoryTotal);
        requestData.put("DownloadBand",InfoSystem.getDownloadBand);
        requestData.put("UploadBand",InfoSystem.getUploadBand);
        requestData.put("DownloadTotal",InfoSystem.getDownloadTotal);
        requestData.put("UploadTotal",InfoSystem.getUploadTotal);
        return requestData;
    }
}
