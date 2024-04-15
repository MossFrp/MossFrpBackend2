package org.mossmc.mosscg.MossFrpBackend.Node;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NodeNumber {
    public static Map<String, List<Integer>> numberUsedListMap = new HashMap<>();

    public static void loadUsedNumber() {
        Map<String, List<Integer>> cacheMap = new HashMap<>();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code");
            while (set.next()) {
                String node = set.getString("node");
                int number = set.getInt("number");
                inputUsedNumber(node,number,numberUsedListMap);
            }
            numberUsedListMap.clear();
            numberUsedListMap = cacheMap;
        } catch (SQLException e) {
            LoggerSender.sendException(e);
        }
    }

    public static void inputUsedNumber(String node, Integer number, Map<String, List<Integer>> map) {
        if (!map.containsKey(node)) {
            map.put(node,new ArrayList<>());
        }
        map.get(node).add(number);
    }

    public static void releaseNumber(String node, Integer number) {
        if (numberUsedListMap.containsKey(node)) {
            numberUsedListMap.get(node).remove(number);
        }
    }

    public static boolean getNumberUsed(String node,int number) {
        if (numberUsedListMap.containsKey(node)) {
            return numberUsedListMap.get(node).contains(number);
        }
        return false;
    }

    public static int getEmptyNumber(String node, Boolean add) {
        int number = BasicInfo.getRandomInt(9999999,1000000);
        while (getNumberUsed(node,number)) {
            number = BasicInfo.getRandomInt(9999999,1000000);
        }
        if (add) {
            inputUsedNumber(node,number,numberUsedListMap);
        }
        return number;
    }
}
