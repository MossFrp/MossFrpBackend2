package org.mossmc.mosscg.MossFrpBackend.Info;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeAPIConnection;
import org.mossmc.mosscg.MossFrpBackend.Web.Request.RequestNodeData;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class InfoSystem {
    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(InfoSystem::updateVoid);
        thread.setName("infoUpdateThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void updateVoid() {
        while (true) {
            try {
                updateInfo();
            } catch (Exception e) {
                sendException(e);
            } finally {
                try {
                    Thread.sleep(1000L*10);
                } catch (InterruptedException e) {
                    sendException(e);
                }
            }
        }
    }
    public static void updateInfo() {
        try {
            SystemInfo info = new SystemInfo();
            HardwareAbstractionLayer hardware = info.getHardware();
            CentralProcessor processor = info.getHardware().getProcessor();

            //内存模块
            long memoryAvailable = hardware.getMemory().getAvailable();
            long memoryTotal = hardware.getMemory().getTotal();
            long memoryUsed = memoryTotal-memoryAvailable;
            double used = (memoryUsed)/1024.0/1024.0/1024.0;
            double total = (memoryTotal)/1024.0/1024.0/1024.0;
            getMemoryUsed = String.format("%.2f",used) + "GB";
            getMemoryTotal = String.format("%.2f",total) + "GB";

            //处理器模块
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            TimeUnit.SECONDS.sleep(1);
            long[] ticks = processor.getSystemCpuLoadTicks();
            long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
            long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
            long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
            long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
            getCPUUsage = new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu));

            //网络模块
            long bytesReceiveStart = 0;
            long bytesSendStart = 0;
            for (NetworkIF net : hardware.getNetworkIFs()) {
                bytesReceiveStart = bytesReceiveStart + net.getBytesRecv();
                bytesSendStart = bytesSendStart + net.getBytesSent();
            }
            TimeUnit.SECONDS.sleep(1);
            long bytesReceiveEnd = 0;
            long bytesSendEnd = 0;
            for (NetworkIF net : hardware.getNetworkIFs()) {
                bytesReceiveEnd = bytesReceiveEnd + net.getBytesRecv();
                bytesSendEnd = bytesSendEnd + net.getBytesSent();
            }
            double upload = (bytesSendEnd-bytesSendStart)/128000.0;
            double download = (bytesReceiveEnd-bytesReceiveStart)/128000.0;
            double uploadTotal = bytesSendEnd/1073741824.0;
            double downloadTotal = bytesReceiveEnd/1073741824.0;
            getUploadBand = String.format("%.2f",upload) + "Mbps";
            getDownloadBand = String.format("%.2f",download) + "Mbps";
            getUploadTotal = String.format("%.2f",uploadTotal) + "GB";
            getDownloadTotal = String.format("%.2f",downloadTotal) + "GB";

            if (BasicInfo.getRunType.equals(Enums.runType.NODE) && BasicInfo.start) {
                NodeAPIConnection.getReturnData(RequestNodeData.getRequest());
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static String getMemoryUsed;
    public static String getMemoryTotal;
    public static String getCPUUsage;
    public static String getUploadBand;
    public static String getDownloadBand;
    public static String getUploadTotal;
    public static String getDownloadTotal;
}
