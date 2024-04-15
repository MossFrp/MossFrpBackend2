package org.mossmc.mosscg.MossFrpBackend.File;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileCheck {
    public static void check() {
        checkDirExist("./MossFrp/Logs");
        if (BasicInfo.getRunType.equals(Enums.runType.NODE)) {
            checkDirExist("./MossFrp/Frps");
            checkDirExist("./MossFrp/Frps/files");
            checkFileExist("./MossFrp/Frps/files/frps", "frps");
            checkFileExist("./MossFrp/Frps/files/frps.exe", "frps.exe");
        }
    }

    public static void checkDirExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                LoggerSender.sendInfo("已创建缺失的文件夹："+path);
            } else {
                LoggerSender.sendError("无法创建缺失的文件夹："+path);
            }
        }
    }

    public static void checkFileExist(String path,String packPath) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                InputStream input = FileCheck.class.getClassLoader().getResourceAsStream(packPath);
                assert input != null;
                Files.copy(input, file.toPath());
                LoggerSender.sendInfo("已创建缺失的文件："+path);
            } catch (IOException e) {
                e.printStackTrace();
                LoggerSender.sendError("无法创建缺失的文件："+path);
            }
        }
    }
}
