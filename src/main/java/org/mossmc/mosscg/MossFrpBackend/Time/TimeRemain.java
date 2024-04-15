package org.mossmc.mosscg.MossFrpBackend.Time;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class TimeRemain {
    public static Long dateRemainTime(String node,String number) {
        JSONObject data = CodeInfo.getCodeInfoByNodeNumber(node,number);
        if (data != null) {
            long deadline = Long.parseLong(data.getString("stop"));
            if (deadline >= System.currentTimeMillis()) {
                return deadline - System.currentTimeMillis();
            }
        }
        return 0L;
    }

    public static int dateRemainDay(String node,String number) {
        long remainTime = dateRemainTime(node, number);
        if (remainTime <= 0L) {
            return 0;
        } else {
            return (int) (remainTime/1000/60/60/24)+1;
        }
    }

    public static String dateRemainString(String node,String number) {
        try {
            long remainTime = dateRemainTime(node, number);
            if (remainTime <= 0L) {
                return "到期";
            } else {
                if (remainTime > 1000*60*60*24) {
                    int remain = (int) (remainTime/1000/60/60/24)+1;
                    return remain+"天";
                }
                if (remainTime > 1000*60*60) {
                    int remain = (int) (remainTime/1000/60/60)+1;
                    return remain+"时";
                }
                if (remainTime > 1000*60) {
                    int remain = (int) (remainTime/1000/60)+1;
                    return remain+"分";
                }
                int remain = (int) (remainTime/1000)+1;
                return remain+"秒";
            }
        } catch (Exception e) {
            sendException(e);
        }
        return null;
    }
}
