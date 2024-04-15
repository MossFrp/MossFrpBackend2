package org.mossmc.mosscg.MossFrpBackend.Logger;

import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class LoggerWriter {
    public static File getLogFile;
    public static BufferedWriter getWriter;

    public static void loadLogger() {
        try {
            getLogFile = new File("./MossFrp/Logs/"+TimeString.getNowTimeString(Enums.timeStringType.LOG)+".yml");
            getWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getLogFile.toPath()), StandardCharsets.UTF_8));
        } catch (Exception e) {
            sendException(e);
            sendError("无法加载日志模块");
        }
    }

    public static void logMessage(String message) {
        if (getWriter == null) {
            return;
        }
        try {
            getWriter.write(message+"\r\n");
            getWriter.flush();
        } catch (IOException e) {
            sendException(e);
        }
    }
}
