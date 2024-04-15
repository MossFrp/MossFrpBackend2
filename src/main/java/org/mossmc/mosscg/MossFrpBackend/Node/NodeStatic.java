package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class NodeStatic {
    public static Map<String, JSONObject> nodeStatic = new HashMap<>();
    public static Map<String, JSONObject> nodeData = new HashMap<>();

    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread staticUpdate = new Thread(NodeStatic::autoUpdate);
        staticUpdate.setName("nodeStaticUpdateThread");
        singleThreadExecutor.execute(staticUpdate::start);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void autoUpdate() {
        while (true) {
            try {
                updateStatic();
                Thread.sleep(1000L*60);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void updateStatic() {
        Map<String,JSONObject> staticCache = new HashMap<>();
        Map<String,Integer> codeCountCache = new HashMap<>();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where status='run'");
            while (set.next()) {
                String user = set.getString("user");
                if (!codeCountCache.containsKey(user)) {
                    codeCountCache.put(user,1);
                } else {
                    codeCountCache.replace(user,codeCountCache.get(user)+1);
                }
                String node = set.getString("node");
                if (!staticCache.containsKey(node)) {
                    staticCache.put(node,new JSONObject());
                    staticCache.get(node).put("user",0);
                    staticCache.get(node).put("band",0);
                }
                int newUser = staticCache.get(node).getInteger("user")+1;
                int newBand = staticCache.get(node).getInteger("band")+set.getInt("band");
                staticCache.get(node).replace("user",newUser);
                staticCache.get(node).replace("band",newBand);
            }
            nodeStatic = staticCache;
            UserCache.codeCountCache = codeCountCache;
        } catch (Exception e) {
            sendException(e);
            sendWarn("更新节点统计信息出现错误！");
        }
    }

    public static String getStatic(String node,String key) {
        if (!nodeStatic.containsKey(node)) {
            return "0";
        }
        return nodeStatic.get(node).getOrDefault(key,0).toString();
    }

    public static void updateData(String node,String key,String value) {
        if (value == null) {
            return;
        }
        if (!nodeData.containsKey(node)) {
            nodeData.put(node,new JSONObject());
        }
        nodeData.get(node).put(key,value);
    }

    public static String getData(String node,String key) {
        if (nodeData.containsKey(node)) {
            if (nodeData.get(node).containsKey(key)) {
                return nodeData.get(node).getString(key);
            }
        }
        return "暂无数据";
    }
}
