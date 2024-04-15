package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Web.Request.RequestHeartbeat;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class NodeHeartbeat {
    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(NodeHeartbeat::heartbeatVoid);
        thread.setName("nodeHeartbeatThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void heartbeatVoid() {
        int updateTime = 5;
        while (true) {
            try {
                if (NodeProcess.needUpdate) {
                    NodeProcess.updateProcess();
                }
                Thread.sleep(1000*updateTime);
                try {
                    JSONObject sendData = RequestHeartbeat.getRequest();
                    JSONObject returnData = NodeAPIConnection.getReturnData(sendData);
                    if (returnData != null) {
                        boolean update = returnData.getBoolean("update");
                        if (update) {
                            NodeProcess.updateProcess();
                        }
                    }
                } catch (Exception e) {
                    sendException(e);
                }
                System.gc();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }
}
