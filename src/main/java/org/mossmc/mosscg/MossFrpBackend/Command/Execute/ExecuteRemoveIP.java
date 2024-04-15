package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.User.UserIP;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteRemoveIP {
    public static void execute(String badIP) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("SELECT * FROM user where IP is not NULL");
            int count = 0;
            UserIP.updateIP();
            while (set.next()) {
                String userID = set.getString("userID");
                String userOldIP = set.getString("IP");
                List<String> userIPList = UserIP.stringToList(userOldIP);
                if (userIPList.contains(badIP)) {
                    count++;
                    userIPList.remove(badIP);
                    if (userIPList.size()>0) {
                        String IPData = UserIP.listToString(userIPList);
                        UserInfo.updateInfo("IP",IPData,"userID",userID);
                        sendInfo("update user "+userID+" IP from "+userOldIP+" to "+IPData);
                    }
                }
            }
            sendInfo("complete, updated "+count+" IPs.");
        } catch (SQLException e) {
            e.printStackTrace();
            sendInfo("failed");
        }
        sendInfo("已完成操作！");
    }
}
