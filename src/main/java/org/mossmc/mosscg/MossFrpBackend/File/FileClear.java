package org.mossmc.mosscg.MossFrpBackend.File;

import java.io.File;

import static org.mossmc.mosscg.MossFrpBackend.File.FileCheck.checkFileExist;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class FileClear {
    public static void clear() {
        File file = new File("./MossFrp/Frps");
        clearFrp(file);
        checkFileExist("./MossFrp/Frps/files/frps", "frps");
        checkFileExist("./MossFrp/Frps/files/frps.exe", "frps.exe");
        sendInfo("已完成Frp文件与进程清理");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clearFrp(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return;
            }
            for (File target : fileList) {
                if (target.isDirectory()) {
                    clearFrp(target);
                    continue;
                }
                if (target.getName().contains("frps_")) {
                    target.delete();
                    continue;
                }
                if (target.getName().equals("frps")||target.getName().equals("frps.exe")) {
                    target.delete();
                }
            }
        }
    }
}
