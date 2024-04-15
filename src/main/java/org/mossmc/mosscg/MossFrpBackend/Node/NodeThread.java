package org.mossmc.mosscg.MossFrpBackend.Node;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class NodeThread {
    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(NodeThread::updateVoid);
        thread.setName("nodeUpdateThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void updateVoid() {
        int updateTime = 20;
        int time = 0;
        while (true) {
            try {
                time++;
                Thread.sleep(1000);
                NodeCache.nodeCache.forEach((node, stringStringMap) -> {
                    if (!NodeCache.nodeStatusCache.containsKey(node)) {
                        NodeCache.nodeStatusCache.put(node, Enums.nodeStatusType.ONLINE);
                    }
                });
                if (time >= updateTime) {
                    List<String> offlineNodes = new ArrayList<>();
                    AtomicBoolean nodeOffline = new AtomicBoolean(false);
                    time = 0;
                    NodeCache.nodeCache.forEach((node, stringStringMap) -> {
                        switch (NodeCache.nodeStatusCache.get(node)) {
                            case ONLINE:
                                NodeCache.nodeStatusCache.replace(node, Enums.nodeStatusType.WAIT);
                                break;
                            case WAIT:
                                NodeCache.nodeStatusCache.replace(node, Enums.nodeStatusType.CHECK);
                                break;
                            case CHECK:
                                NodeCache.nodeStatusCache.replace(node, Enums.nodeStatusType.WARNING);
                                break;
                            case WARNING:
                                NodeCache.nodeStatusCache.replace(node, Enums.nodeStatusType.OFFLINE);
                                nodeOffline.set(true);
                                offlineNodes.add(node);
                                sendWarn("节点"+node+"已离线！");
                                break;
                            default:
                                break;
                        }
                    });
                    if (nodeOffline.get()) {
                        StringBuilder message = new StringBuilder("节点");
                        AtomicBoolean needSpilt = new AtomicBoolean(false);
                        offlineNodes.forEach(node -> {
                            if (needSpilt.get()) {
                                message.append("、");
                            }
                            message.append(node);
                            needSpilt.set(true);
                        });
                        message.append("已掉线！请检查节点状态！");
                        BotQQ.sendAdminMessage(message.toString());
                    }
                    NodeCache.refreshCache();
                    System.gc();
                }
            } catch (Exception e) {
                sendException(e);
            }
        }
    }
}
