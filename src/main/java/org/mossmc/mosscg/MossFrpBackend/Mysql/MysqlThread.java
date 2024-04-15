package org.mossmc.mosscg.MossFrpBackend.Mysql;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class MysqlThread {
    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(MysqlThread::updateVoid);
        thread.setName("mysqlUpdateThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void updateVoid() {
        while (true) {
            try {
                Thread.sleep(1000L*Integer.parseInt(BasicInfo.getConfig("mysqlUpdateTime")));
                sendInfo("正在自动刷新Mysql链接");
                MysqlConnection.updatePoolConnection();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }
}
