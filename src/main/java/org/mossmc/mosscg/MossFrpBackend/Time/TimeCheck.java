package org.mossmc.mosscg.MossFrpBackend.Time;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeRefresh;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeRemove;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class TimeCheck {
    public static void checkDateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread dateChecker = new Thread(TimeCheck::codeCheck);
        dateChecker.setName("codeTimeCheckThread");
        singleThreadExecutor.execute(dateChecker);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void codeCheck() {
        while (true) {
            try {
                runCheck();
            } catch (Exception e) {
                LoggerSender.sendException(e);
            } finally {
                try {
                    Thread.sleep(1000L*60);
                } catch (InterruptedException e) {
                    sendException(e);
                }
            }
        }
    }

    public static synchronized void runCheck() throws Exception{
        ResultSet set = MysqlGetResult.getResultSet("select * from code");
        while (set.next()) {
            try {
                String codeID = set.getString("ID");
                String codeStatus = set.getString("status");
                String node = set.getString("node");
                String port = set.getString("port");
                String number = set.getString("number");
                String user = set.getString("user");
                String band = set.getString("band");
                String codeData = node + " | " + number + " | " + user + " | " + band;
                long deadline = set.getLong("stop");
                switch (codeStatus) {
                    case "run":
                        if (port.equals("0")) {
                            CodeRefresh.refreshExecute(node,port,codeID,number);
                            continue;
                        }
                        if (checkOneDay(deadline) && set.getString("warning").equals("false")) {
                            String email = getEmail(set.getString("user"));
                            MailSend.sendOneDay(email,node,number);
                            sendInfo("检测到编号为" + codeID + "的frp即将在一天后到期！参数：" + codeData);
                            CodeInfo.updateCodeInfo("ID",codeID,"warning","true");
                            continue;
                        }
                        if (!checkDate(deadline)) {
                            String email = getEmail(set.getString("user"));
                            MailSend.sendOutdated(email,node,number);
                            sendInfo("检测到编号为" + codeID + "的frp已到期！参数：" + codeData);
                            CodeInfo.updateCodeInfo("ID",codeID,"status","outdated");
                            continue;
                        }
                        break;
                    case "outdated":
                        if (checkOutTime(deadline,7)) {
                            String email = getEmail(set.getString("user"));
                            MailSend.sendRemoved(email,node,number);
                            sendInfo("检测到编号为" + codeID + "的frp已到期7天，已自动删除！参数：" + codeData);
                            CodeRemove.remove(CodeInfo.getCodeInfo("ID",codeID));
                            continue;
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LoggerSender.sendException(e);
            }
        }
    }

    public static String getEmail(String userID) {
        JSONObject data = UserInfo.getUserInfo("userID",userID);
        assert data != null;
        return data.getString("email");
    }

    public static Boolean checkDate(long date) {
        return date > System.currentTimeMillis();
    }

    public static Boolean checkOutTime(long date,int day) {
        long except = date + 1000L *60*60*24*day;
        return except < System.currentTimeMillis();
    }

    public static Boolean checkOneDay(long date) {
        return date-24*60*60*1000 < System.currentTimeMillis();
    }
}
