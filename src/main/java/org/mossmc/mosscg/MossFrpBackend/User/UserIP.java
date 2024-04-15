package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class UserIP {
    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread IPUpdate = new Thread(UserIP::autoUpdate);
        IPUpdate.setName("userIPUpdateThread");
        singleThreadExecutor.execute(IPUpdate::start);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void autoUpdate() {
        while (true) {
            try {
                updateIP();
                Thread.sleep(1000L * 60);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public synchronized static void updateIP() {
        try {
            Map<String, List<String>> userIPCache = new HashMap<>(userIPMap);
            userIPMap.clear();
            userIPCache.forEach(((userID, IPList) -> {
                try {
                    ResultSet set = MysqlGetResult.getResultSet("SELECT * FROM user where userID=?",userID);
                    if (set.next()) {
                        List<String> IPData;
                        String dataString = set.getString("IP");
                        if (dataString != null) {
                            IPData = stringToList(dataString);
                        } else {
                            IPData = new ArrayList<>();
                        }
                        IPList.forEach((IP -> {
                            if (!IPData.contains(IP)) {
                                IPData.add(IP);
                            }
                        }));
                        IPData.remove("null");
                        dataString = listToString(IPData);
                        UserInfo.updateInfo("userID",userID,"IP",dataString);
                    }
                } catch (SQLException e) {
                    sendException(e);
                }
            }));
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static Map<String, List<String>> userIPMap = new HashMap<>();

    public static void logWebIP(String token,String IP) {
        if (IP == null) return;
        if (IP.equals("null")) return;
        if (IP.equals("127.0.0.1")) return;
        String userID = TokenCache.tokenUserMap.get(token);
        if (!userIPMap.containsKey(userID)) {
            userIPMap.put(userID,new ArrayList<>());
        }
        userIPMap.get(userID).add(IP);
    }

    public static String listToString(List<String> data) {
        return String.join(",", data);
    }

    public static List<String> stringToList(String data) {
        return new ArrayList(Arrays.asList(data.split(",")));
    }

    public static void checkSameIP(int max) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("SELECT * FROM user where IP is not NULL");
            Map<String,List<String>> IPUserMap = new HashMap<>();
            while (set.next()) {
                String user = set.getString("userID");
                List<String> userIPList = stringToList(set.getString("IP"));
                userIPList.forEach(IP -> {
                    if (!IPUserMap.containsKey(IP)) {
                        IPUserMap.put(IP,new ArrayList<>());
                    }
                    IPUserMap.get(IP).add(user);
                });
            }
            IPUserMap.forEach((IP, users) -> {
                if (users.size() >= max) {
                    sendInfo("IP：["+IP+"]有"+users.size()+"位IP相同的用户："+listToString(users));
                }
            });
        } catch (Exception e) {
            sendException(e);
        }
    }
}
