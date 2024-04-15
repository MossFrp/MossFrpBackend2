package org.mossmc.mosscg.MossFrpBackend.Node;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NodePort {
    public static Map<String, List<Integer>> portEmptyListMap = new HashMap<>();

    public static void loadEmptyPort() {
        Map<String, List<Integer>> cacheMap = new HashMap<>();
        NodeCache.nodeCache.forEach((node, infoMap) -> {
            String[] port = infoMap.get("port").split("-");
            int portStart = Integer.parseInt(port[0]);
            int portEnd = Integer.parseInt(port[1]);
            while (portStart < portEnd) {
                inputEmptyPort(node,portStart,cacheMap);
                portStart = portStart + 10;
            }
        });
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code");
            while (set.next()) {
                String node = set.getString("node");
                int port = set.getInt("port");
                removeUsedPort(node,port,cacheMap);
            }
            portEmptyListMap.clear();
            portEmptyListMap = cacheMap;
        } catch (SQLException e) {
            LoggerSender.sendException(e);
        }
    }

    public static void inputEmptyPort(String node, Integer port, Map<String, List<Integer>> map) {
        if (!map.containsKey(node)) {
            map.put(node,new ArrayList<>());
        }
        map.get(node).add(port);
    }

    public static void removeUsedPort(String node, Integer port, Map<String, List<Integer>> map) {
        if (map.containsKey(node)) {
            map.get(node).remove(port);
        }
    }

    public static int getEmptyPort(String node, Boolean remove) {
        int port = portEmptyListMap.get(node).get(BasicInfo.getRandomInt(portEmptyListMap.get(node).size()-1,0));
        if (remove) {
            removeUsedPort(node, port, portEmptyListMap);
        }
        return port;
    }
}
