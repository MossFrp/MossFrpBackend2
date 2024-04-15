package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Main;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteClearCache {
    public static void execute() {
        long memoryUsage = Runtime.getRuntime().freeMemory();
        Main.refreshCache();
        long newMemoryUsage = Runtime.getRuntime().freeMemory();
        double free = (double) (newMemoryUsage - memoryUsage);
        free = free / 1024;
        free = free / 1024;
        sendInfo("已刷新MossFrp后端缓存，回收内存"+String.format("%.2f", free)+"MB");
    }
}
