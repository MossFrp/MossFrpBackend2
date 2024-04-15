package org.mossmc.mosscg.MossFrpBackend.Time;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimeString {
    public static SimpleDateFormat getLogFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static SimpleDateFormat getFullFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat getDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat getTimeFormat=new SimpleDateFormat("HH:mm:ss");

    public static String getNowTimeString(Enums.timeStringType type) {
        return getTimeString(type,System.currentTimeMillis());
    }

    public static String getTimeString(Enums.timeStringType type, Long timeStamp) {
        switch (type) {
            case FULL:
                return getFullFormat.format(new Date(timeStamp));
            case DATE:
                return getDateFormat.format(new Date(timeStamp));
            case TIME:
                return getTimeFormat.format(new Date(timeStamp));
            case LOG:
                return getLogFormat.format(new Date(timeStamp));
            default:
                return null;
        }
    }
}
