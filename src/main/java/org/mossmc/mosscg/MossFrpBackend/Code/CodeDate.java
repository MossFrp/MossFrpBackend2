package org.mossmc.mosscg.MossFrpBackend.Code;

import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeAdd;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class CodeDate {
    public static List<String> addNodeAllDay(String node, int day) {
        try {
            List<String> result = new ArrayList<>();
            ResultSet set = MysqlGetResult.getResultSet("select  * from code where status='run' and node='"+node+"'");
            while (set.next()) {
                String number = set.getString("number");
                String ID = set.getString("ID");
                TimeAdd.dateAddDay(day,node,number);
                sendInfo("已为"+ID+"["+node+"|"+number+"]增加了"+day+"天时长！");
                result.add(ID);
            }
            return result;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static List<String> addAllDay(int day) {
        try {
            List<String> result = new ArrayList<>();
            ResultSet set = MysqlGetResult.getResultSet("select  * from code where status='run'");
            while (set.next()) {
                String number = set.getString("number");
                String node = set.getString("node");
                String ID = set.getString("ID");
                TimeAdd.dateAddDay(day,node,number);
                sendInfo("已为"+ID+"["+node+"|"+number+"]增加了"+day+"天时长！");
                result.add(ID);
            }
            return result;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }
}
