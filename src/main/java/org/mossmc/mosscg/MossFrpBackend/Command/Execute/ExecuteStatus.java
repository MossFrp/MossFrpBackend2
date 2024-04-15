package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteStatus {
    public static void execute(Enums.runType type) {
        long memoryTotal = Runtime.getRuntime().totalMemory();
        long memoryMax = Runtime.getRuntime().maxMemory();
        long memoryUsage = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long memoryFree = Runtime.getRuntime().freeMemory();

        double total = (double) memoryTotal/(1024*1024);
        double max = (double) memoryMax/(1024*1024);
        double usage = (double) memoryUsage/(1024*1024);
        double free = (double) memoryFree/(1024*1024);

        String totalString = String.format("%.2f", total)+"MB[总量]";
        String maxString = String.format("%.2f", max)+"MB[最大]";
        String usageString = String.format("%.2f", usage)+"MB[使用]";
        String freeString = String.format("%.2f", free)+"MB[剩余]";

        sendInfo("MossFrp后端状态");
        sendInfo("内存占用："+totalString+"/"+maxString+"/"+usageString+"/"+freeString);
    }
}
