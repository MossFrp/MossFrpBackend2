package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.File.FileCheck.checkDirExist;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Time.TimeString.getNowTimeString;

public class NodeReliable {
    public static Map<String,Integer> reliableSuccess;
    public static Map<String,Integer> reliableTotal;
    public static Map<String,Integer> reliableDailySuccess;
    public static Map<String,Integer> reliableDailyTotal;
    public static String lastDate;

    public static void runThread() {
        readCache();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(NodeReliable::reliableThread);
        thread.setName("nodeReliableThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void reliableThread() {
        while (true) {
            try {
                Thread.sleep(60 * 1000L);
                checkAll();
                writeCache();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void checkAll() {
        for (Map.Entry<String, Map<String, String>> entry : NodeCache.nodeCache.entrySet()) {
            String node = entry.getKey();
            Map<String, String> nodeDataMap = entry.getValue();
            if (!nodeDataMap.get("enable").equals("true")) continue;
            Enums.nodeStatusType status = NodeCache.nodeStatusCache.getOrDefault(node, Enums.nodeStatusType.OFFLINE);
            if (status != Enums.nodeStatusType.OFFLINE) {
                addData(node, 1, 1);
            } else {
                addData(node, 1, 0);
            }
        }
    }

    public static void addData(String node,int total,int success) {
        if (!reliableSuccess.containsKey(node)) reliableSuccess.put(node,0);
        if (!reliableTotal.containsKey(node)) reliableTotal.put(node,0);
        if (!reliableDailySuccess.containsKey(node)) reliableDailySuccess.put(node,0);
        if (!reliableDailyTotal.containsKey(node)) reliableDailyTotal.put(node,0);
        reliableSuccess.replace(node,reliableSuccess.get(node)+success);
        reliableTotal.replace(node,reliableTotal.get(node)+total);
        reliableDailySuccess.replace(node,reliableDailySuccess.get(node)+success);
        reliableDailyTotal.replace(node,reliableDailyTotal.get(node)+total);
    }

    public static void writeCache() {
        try {
            BufferedWriter writerTotal = new BufferedWriter(new FileWriter("./MossFrp/Reliable/total.yml"));
            for (Map.Entry<String, Integer> entry : reliableTotal.entrySet()) {
                String node = entry.getKey();
                int total = entry.getValue();
                int success = reliableSuccess.get(node);
                writerTotal.write(node + ": " + success + "|" + total);
                writerTotal.write("\r\n");
                writerTotal.flush();
            }
            writerTotal.close();

            BufferedWriter writerDaily = new BufferedWriter(new FileWriter("./MossFrp/Reliable/"+lastDate+".yml"));
            for (Map.Entry<String, Integer> entry : reliableDailyTotal.entrySet()) {
                String node = entry.getKey();
                int total = entry.getValue();
                int success = reliableDailySuccess.get(node);
                writerDaily.write(node + ": " + success + "|" + total);
                writerDaily.write("\r\n");
                writerDaily.flush();
            }
            writerDaily.close();

            String newDate = getNowTimeString(Enums.timeStringType.DATE);
            if (!newDate.equals(lastDate)) {
                lastDate = newDate;
                reliableDailySuccess.clear();
                reliableDailyTotal.clear();
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void readCache() {
        reliableSuccess = new HashMap<>();
        reliableTotal = new HashMap<>();
        reliableDailySuccess = new HashMap<>();
        reliableDailyTotal = new HashMap<>();
        try {
            checkDirExist("./MossFrp/Reliable");
            //数据存储格式：sq1: 100|200
            File totalFile = new File("./MossFrp/Reliable/total.yml");
            if (totalFile.exists()) {
                Yaml yaml = new Yaml();
                FileInputStream input = new FileInputStream(totalFile);
                Map<?,?> totalData = yaml.loadAs(input, Map.class);
                totalData.forEach((obj1, obj2) -> {
                    String[] cut = obj2.toString().split("\\|");
                    reliableSuccess.put(obj1.toString(),Integer.valueOf(cut[0]));
                    reliableTotal.put(obj1.toString(),Integer.valueOf(cut[1]));
                });
            }
            //数据存储格式：sq1: 100|200
            lastDate = getNowTimeString(Enums.timeStringType.DATE);
            File dailyFile = new File("./MossFrp/Reliable/"+lastDate+".yml");
            if (dailyFile.exists()) {
                Yaml yaml = new Yaml();
                FileInputStream input = new FileInputStream(dailyFile);
                Map<?,?> dailyData = yaml.loadAs(input, Map.class);
                dailyData.forEach((obj1, obj2) -> {
                    String[] cut = obj2.toString().split("\\|");
                    reliableDailySuccess.put(obj1.toString(),Integer.valueOf(cut[0]));
                    reliableDailyTotal.put(obj1.toString(),Integer.valueOf(cut[1]));
                });
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static JSONObject getReliableData(String node) {
        JSONObject data = new JSONObject();
        double resultTotal = reliableSuccess.getOrDefault(node, 0) * 1.0 / reliableTotal.getOrDefault(node, 1);
        double resultDaily = reliableDailySuccess.getOrDefault(node, 0) * 1.0 / reliableDailyTotal.getOrDefault(node, 1);
        data.put("reliableTotal",String.format("%.4f",resultTotal));
        data.put("reliableDaily",String.format("%.4f",resultDaily));
        data.put("reliableTotalData",reliableSuccess.getOrDefault(node, 0)+"|"+reliableTotal.getOrDefault(node, 0));
        data.put("reliableDailyData",reliableDailySuccess.getOrDefault(node, 0)+"|"+reliableDailyTotal.getOrDefault(node, 0));
        return data;
    }
}
