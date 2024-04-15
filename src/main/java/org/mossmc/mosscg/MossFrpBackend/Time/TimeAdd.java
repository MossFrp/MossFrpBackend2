package org.mossmc.mosscg.MossFrpBackend.Time;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class TimeAdd {
    public static String dateAddDay(int time,String node,String numberString) {
        return dateAdd(time*24*60*60*1000L,node,numberString);
    }

    public static String dateAdd(long time,String node,String numberString) {
        try {
            int number = Integer.parseInt(numberString);
            JSONObject jsonObject = CodeInfo.getCodeInfoByNodeNumber(node,numberString);
            if (jsonObject != null) {
                long deadline = jsonObject.getLong("stop");
                if (!jsonObject.getString("status").equals("run")) {
                    deadline = System.currentTimeMillis();
                }
                deadline = deadline + time;
                CodeInfo.updateCodeInfo("ID",jsonObject.getString("ID"),"stop", String.valueOf(deadline));
                CodeInfo.updateCodeInfo("ID",jsonObject.getString("ID"),"warning", "false");
                CodeInfo.updateCodeInfo("ID",jsonObject.getString("ID"),"status", "run");
                if (NodeCache.nodeCache.get(node).get("activity").equals("true")) {
                    CodeInfo.updateCodeInfo("ID",jsonObject.getString("ID"),"activity", "true");
                }
                sendInfo( "更新穿透码"+node+" | "+number+"成功！下次到期时间：" + TimeString.getTimeString(Enums.timeStringType.FULL,deadline));
                NodeCache.nodeUpdate.add(node);
                return "success";
            } else {
                return null;
            }
        } catch (Exception e) {
            sendException(e);
            return "failed";
        }
    }
}
