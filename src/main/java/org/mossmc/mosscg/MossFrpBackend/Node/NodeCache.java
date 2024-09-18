package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.*;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class NodeCache {
    public static Map<String, Map<String, String>> nodeCache = new HashMap<>();
    public static Map<String, Enums.nodeStatusType> nodeStatusCache = new HashMap<>();
    public static Map<String, String> nodeVersionCache = new HashMap<>();
    public static List<String> nodeUpdate = new ArrayList<>();
    public static List<String> nodeList = new ArrayList<>();

    public static JSONObject getLightNodeInfo() {
        JSONObject data = new JSONObject();
        nodeCache.forEach((node, nodeDataMap) -> {
            if (nodeDataMap.get("enable").equals("true")) {
                JSONObject nodeData = new JSONObject();
                nodeData.put("node", node);
                nodeData.put("address", nodeDataMap.get("address"));
                nodeData.put("status", getNodeStatusName(nodeStatusCache.getOrDefault(node, Enums.nodeStatusType.OFFLINE)));
                nodeData.put("enable", nodeDataMap.get("enable"));
                nodeData.put("activity", nodeDataMap.get("activity"));
                nodeData.put("coin", nodeDataMap.get("coin"));
                nodeData.put("price", nodeDataMap.get("price"));
                nodeData.put("band-max-per", nodeDataMap.get("band-max-per"));
                nodeData.put("prefix", nodeDataMap.get("prefix"));
                nodeData.put("info", nodeDataMap.get("prefix")+nodeDataMap.get("info"));
                nodeData.put("load", nodeDataMap.get("load"));
                nodeData.put("uploadTotal", NodeStatic.getData(node, "UploadTotal"));
                nodeData.put("downloadTotal", NodeStatic.getData(node, "DownloadTotal"));
                nodeData.put("uploadBand", NodeStatic.getData(node, "UploadBand"));
                nodeData.put("downloadBand", NodeStatic.getData(node, "DownloadBand"));
                nodeData.put("CPUUsage", NodeStatic.getData(node, "CPUUsage"));
                nodeData.put("memoryUsed", NodeStatic.getData(node, "MemoryUsed"));
                nodeData.put("memoryTotal", NodeStatic.getData(node, "MemoryTotal"));
                nodeData.put("name", node+"-"+nodeDataMap.get("address"));
                nodeData.putAll(NodeReliable.getReliableData(node));
                data.put(node,nodeData);
            }
        });
        return data;
    }

    public static void refreshCache() {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from node");
            Map<String, Map<String, String>> nodeCacheNew = new HashMap<>();
            Map<Integer,List<String>> nodePrefixCache = new HashMap<>();
            List<Integer> nodeSizeCache = new ArrayList<>();
            List<String> nodeListNew = new ArrayList<>();
            while (set.next()) {
                String node = set.getString("node");
                String load = getNodeLoad(node, set.getString("user-max"), set.getString("band-max-total"), NodeStatic.getStatic(node, "user"), NodeStatic.getStatic(node, "band"));
                inputCache(nodeCacheNew, node, "address", set.getString("address"));
                inputCache(nodeCacheNew, node, "provider", set.getString("provider"));
                inputCache(nodeCacheNew, node, "enable", set.getString("enable"));
                inputCache(nodeCacheNew, node, "activity", set.getString("activity"));
                inputCache(nodeCacheNew, node, "coin", set.getString("coin"));
                inputCache(nodeCacheNew, node, "price", set.getString("price"));
                inputCache(nodeCacheNew, node, "band-max-per", set.getString("band-max-per"));
                inputCache(nodeCacheNew, node, "band-max-node", set.getString("band-max-node"));
                inputCache(nodeCacheNew, node, "band-max-total", set.getString("band-max-total"));
                inputCache(nodeCacheNew, node, "user-max", set.getString("user-max"));
                inputCache(nodeCacheNew, node, "port", set.getString("port"));
                inputCache(nodeCacheNew, node, "password", set.getString("password"));
                inputCache(nodeCacheNew, node, "prefix", set.getString("prefix"));
                inputCache(nodeCacheNew, node, "info", set.getString("info"));
                inputCache(nodeCacheNew, node, "load", load);
                if (set.getString("enable").equals("true")) {
                    int length = set.getString("prefix").length();
                    if (!nodePrefixCache.containsKey(length)) {
                        nodePrefixCache.put(length,new ArrayList<>());
                        nodeSizeCache.add(length);
                    }
                    nodePrefixCache.get(length).add(node);
                }
            }
            nodeCache.clear();
            nodeCache = nodeCacheNew;
            Collections.sort(nodeSizeCache);
            for (int i = nodeSizeCache.size()-1;i>=0;i--) {
                nodeListNew.addAll(nodePrefixCache.get(nodeSizeCache.get(i)));
            }
            nodeList.clear();
            nodeList = nodeListNew;
            //LoggerSender.sendInfo("已更新节点信息缓存！"+nodeList);
        } catch (SQLException e) {
            sendException(e);
        }
    }

    public static void inputCache(Map<String, Map<String, String>> cacheMap, String node, String key, String value) {
        if (!cacheMap.containsKey(node)) {
            cacheMap.put(node, new HashMap<>());
        }
        cacheMap.get(node).put(key, value);
    }

    public static String getNodeLoad(String node, String maxUser, String maxBand, String nowUser, String nowBand) {
        double basicLoad;
        double nowLoad;
        try {
            double userMax = Integer.parseInt(maxUser);
            double bandMax = Integer.parseInt(maxBand);
            double userNow = Integer.parseInt(nowUser);
            double bandNow = Integer.parseInt(nowBand);
            //double load = (userNow/userMax)*(bandNow/bandMax)*100;这是旧的负载算法
            basicLoad = ((userNow * 10 + bandNow) / (userMax * 10 + bandMax)) * 100;
        } catch (Exception e) {
            sendException(e);
            return "Error";
        }
        if (NodeStatic.nodeData.containsKey(node)) {
            try {
                double bandMax = Integer.parseInt(nodeCache.get(node).get("band-max-node"));
                double bandNow = Double.parseDouble(NodeStatic.getData(node, "UploadBand").replace("Mbps", ""));
                nowLoad = (bandNow / bandMax) * 100;
                if (nowLoad >= 100.00) {
                    nowLoad = 100.00;
                }
                return String.format("%.2f", basicLoad)+"% | "+String.format("%.2f", nowLoad)+"%";
            } catch (Exception e) {
                sendException(e);
                return "Error";
            }
        }
        return String.format("%.2f", basicLoad) + "%";
    }

    public static String getNodeStatus(String node, String mode) {
        StringBuilder message = new StringBuilder("【MossFrp 节点信息】");
        if (node.equals("all")) {
            for (String nodeName : nodeCache.keySet()) {
                if (nodeCache.get(nodeName).get("enable").equals("false")) {
                    continue;
                }
                message.append(getNodeStatusString(nodeName, mode));
            }
        } else {
            if (!nodeCache.containsKey(node)) {
                return "未知的节点！";
            }
            message.append(getNodeStatusString(node, mode));
        }
        return message.toString();
    }

    public static String getNodeData(String key,String filter) {
        StringBuilder message = new StringBuilder("【MossFrp 节点数据】");
        for (String nodeName : nodeCache.keySet()) {
            if (nodeCache.get(nodeName).get("enable").equals("false")) {
                continue;
            }
            String info = nodeCache.get(nodeName).get(key);
            if (key.equals("band") || key.equals("user")) {
                info = NodeStatic.getStatic(nodeName, key);
            }
            if (key.equals("status")) {
                info = nodeStatusCache.get(nodeName).name();
            }
            if (key.equals("version")) {
                info = nodeVersionCache.getOrDefault(nodeName, "nope");
            }
            if (key.equals("reliable")) {
                JSONObject data = NodeReliable.getReliableData(nodeName);
                info = data.getString("reliableTotal")+data.getString("reliableDaily");
            }
            if (key.equals("reliableData")) {
                JSONObject data = NodeReliable.getReliableData(nodeName);
                info = data.getString("reliableTotalData").replace("|"," | ")+" & "+data.getString("reliableDailyData").replace("|"," | ");
            }
            if (info == null) {
                info = NodeStatic.getData(nodeName,key);
            }
            if (filter != null) {
                if (filter.startsWith("!")) {
                    if (("!"+info).equals(filter)) {
                        continue;
                    }
                } else {
                    if (!info.equals(filter)) {
                        continue;
                    }
                }
            }
            message.append("\r\n[").append(nodeName).append("]: ").append(info);
        }
        return message.toString();
    }

    public static String getNodeStatusString(String node, String mode) {

        Enums.nodeStatusType nodeStatusType = nodeStatusCache.getOrDefault(node, Enums.nodeStatusType.OFFLINE);
        Enums.coinType nodeCoinType = Enums.coinType.valueOf(nodeCache.get(node).get("coin").toUpperCase());
        JSONObject reliableData = NodeReliable.getReliableData(node);

        String statusName = getNodeStatusName(nodeStatusType);
        String coinName = getCoinTypeName(nodeCoinType);

        String address = nodeCache.get(node).get("address");

        StringBuilder message;

        if (mode.equals("light")) {
            message = new StringBuilder("\r\n[" + statusName + "][" + node + "节点][" + address + "]");
        } else {
            message = new StringBuilder("\r\n-=*[" + statusName + "][" + node + "节点][" + address + "]*=-");
        }
        message.append("\r\n理论负载：").append(nodeCache.get(node).get("load"));
        message.append("\r\n可用占比：").append(reliableData.getString("reliableTotal")).append("|").append(reliableData.getString("reliableDaily"));
        message.append("\r\n带宽价格：").append(nodeCache.get(node).get("price")).append(coinName).append("/Mbps/天");
        if (!mode.equals("full")) {
            return message.toString();
        }
        message.append("\r\n最大带宽：").append(nodeCache.get(node).get("band-max-per")).append(" | ").append(nodeCache.get(node).get("band-max-node"));
        message.append("\r\n节点状态：").append(nodeStatusCache.get(node).name());
        message.append("\r\n可用端口：").append(nodeCache.get(node).get("port"));
        message.append("\r\n用户数量：").append(NodeStatic.getStatic(node, "user")).append("/").append(nodeCache.get(node).get("user-max"));
        message.append("\r\n配额用量：").append(NodeStatic.getStatic(node, "band")).append("/").append(nodeCache.get(node).get("band-max-total"));
        message.append("\r\n核心负载：").append(NodeStatic.getData(node, "CPUUsage")).append("%");
        message.append("\r\n内存占用：").append(NodeStatic.getData(node, "MemoryUsed")).append("/").append(NodeStatic.getData(node, "MemoryTotal"));
        message.append("\r\n节点带宽：↑").append(NodeStatic.getData(node, "UploadBand")).append("/↓").append(NodeStatic.getData(node, "DownloadBand"));
        message.append("\r\n节点流量：↑").append(NodeStatic.getData(node, "UploadTotal")).append("/↓").append(NodeStatic.getData(node, "DownloadTotal"));
        message.append("\r\n提供用户：").append(nodeCache.get(node).get("provider"));
        message.append("\r\n节点头衔：").append(nodeCache.get(node).get("prefix"));
        message.append("\r\n备注信息：").append(nodeCache.get(node).get("info"));
        message.append("\r\n软件版本：").append(nodeVersionCache.getOrDefault(node, "nope"));

        return message.toString();
    }
}