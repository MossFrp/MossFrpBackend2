package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class UserCache {
    public static List<String> nameCache = new ArrayList<>();
    public static List<String> emailCache = new ArrayList<>();
    public static List<String> qqCache = new ArrayList<>();
    public static Map<String,Integer> codeCountCache = new HashMap<>();
    public static int userCount = 0;
    public static int codeCount = 0;
    public static int nodeCount = 0;

    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(UserCache::updateCache);
        thread.setName("userInfoThread");
        singleThreadExecutor.execute(thread);
    }
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void updateCache() {
        try {
            while (true) {
                Thread.sleep(1000L*60*10);
                loadCache();
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void loadCache() {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from user");
            List<String> nameCacheNew = new ArrayList<>();
            List<String> emailCacheNew = new ArrayList<>();
            List<String> qqCacheNew = new ArrayList<>();

            while (set.next()) {
                nameCacheNew.add(set.getString("username"));
                emailCacheNew.add(set.getString("email"));
                qqCacheNew.add(set.getString("qq"));
            }

            nameCache.clear();
            nameCache = nameCacheNew;
            emailCache.clear();
            emailCache = emailCacheNew;
            qqCache.clear();
            qqCache = qqCacheNew;

            set = MysqlGetResult.getResultSet("select count(userID) from user");
            set.next();
            userCount = set.getInt(1);
            set = MysqlGetResult.getResultSet("select count(*) from code where status = 'run'");
            set.next();
            codeCount = set.getInt(1);
            set = MysqlGetResult.getResultSet("select count(*) from node where enable = 'true'");
            set.next();
            nodeCount = set.getInt(1);
        } catch (SQLException e) {
            sendException(e);
        }
    }

    public static int getUserCodeCount(String userID) {
        return codeCountCache.getOrDefault(userID,0);
    }
}
