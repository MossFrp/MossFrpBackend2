package org.mossmc.mosscg.MossFrpBackend.Info;

import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

public class InfoLoad {
    public static void sendLoadStart(String module) {
        LoggerSender.sendInfo("正在加载"+module+"模块");
    }

    public static void sendLoadComplete(String module) {
        LoggerSender.sendInfo("已完成"+module+"模块加载");
    }
}
