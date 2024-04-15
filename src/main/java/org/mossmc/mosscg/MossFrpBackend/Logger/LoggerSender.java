package org.mossmc.mosscg.MossFrpBackend.Logger;

import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBasic;

import java.io.*;

public class LoggerSender {
    public static void sendInfo(String message) {
        sendMessage(getPrefix(Enums.infoType.INFO)+message);
    }

    public static void sendWarn(String message) {
        sendMessage(getPrefix(Enums.infoType.WARN)+message);
    }

    public static void sendError(String message) {
        sendMessage(getPrefix(Enums.infoType.ERROR)+message);
    }

    public static void sendException(Exception exception) {
        readException(exception);
    }

    public static void sendAPI(String message,Enums.typeAPI typeAPI) {
        switch (typeAPI) {
            case NODE:
                if (WebBasic.displayNodeAPI) {
                    sendMessage(getPrefix(Enums.infoType.API)+message);
                    return;
                }
                break;
            case CLIENT:
                if (WebBasic.displayClientAPI) {
                    sendMessage(getPrefix(Enums.infoType.API)+message);
                    return;
                }
                break;
        }
        message = "["+TimeString.getNowTimeString(Enums.timeStringType.FULL)+"]"+getPrefix(Enums.infoType.API)+message;
        LoggerWriter.logMessage(message);
    }

    public static void sendCardUsage(String message) {
        sendMessage(getPrefix(Enums.infoType.CARD)+message);
    }

    public static void sendCommand(String message) {
        sendMessage(getPrefix(Enums.infoType.COMMAND)+message);
    }

    public static void sendMessage(String message) {
        message = "["+TimeString.getNowTimeString(Enums.timeStringType.FULL)+"]"+message;
        //System.out.println(message);
        fastOut(message);
        LoggerWriter.logMessage(message);
    }

    public static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

    public static void fastOut(String message) {
        try {
            out.write(message);
            out.write("\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPrefix(Enums.infoType type) {
        switch (type) {
            case INFO:
                return "[Info]";
            case WARN:
                return "[Warn]";
            case ERROR:
                return "[Error]";
            case EXCEPTION:
                return "[Exception]";
            case API:
                return "[API]";
            case COMMAND:
                return "[Command]";
            case CARD:
                return "[Card]";
            default:
                return null;
        }
    }

    public static void readException(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter= new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        sendMessage(getPrefix(Enums.infoType.EXCEPTION)+stringWriter);
        try {
            printWriter.close();
            stringWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
