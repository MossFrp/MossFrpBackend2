package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeReliable;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;

import java.util.*;

public class ReplyReliable {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 300) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 1) {
            send.append("指令帮助：#SLA <daily/total> 或#SLA <node>");
            return;
        }
        Map<String,Double> cacheMap = new LinkedHashMap<>();
        switch (command[1]) {
            case "daily":
                send.append("MossFrp 当日SLA统计数据");
                for (Map.Entry<String, Map<String, String>> entry : NodeCache.nodeCache.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> value = entry.getValue();
                    if (!value.get("enable").equals("true")) continue;
                    cacheMap.put(key, NodeReliable.getReliableData(key).getDouble("reliableDaily"));
                }
                List<Map.Entry<String,Double>> listDaily = new ArrayList<>(cacheMap.entrySet());
                listDaily.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                for (Map.Entry<String, Double> data : listDaily) {
                    send.append("\r\n").append(data.getKey()).append(": ");
                    send.append(String.format("%.2f",data.getValue()*100)).append("%");
                }                break;
            case "total":
                send.append("MossFrp 累计SLA统计数据");
                for (Map.Entry<String, Map<String, String>> entry : NodeCache.nodeCache.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> value = entry.getValue();
                    if (!value.get("enable").equals("true")) continue;
                    cacheMap.put(key, NodeReliable.getReliableData(key).getDouble("reliableTotal"));
                }
                List<Map.Entry<String,Double>> listTotal = new ArrayList<>(cacheMap.entrySet());
                listTotal.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                for (Map.Entry<String, Double> data : listTotal) {
                    send.append("\r\n").append(data.getKey()).append(": ");
                    send.append(String.format("%.2f",data.getValue()*100)).append("%");
                }
                break;
            default:
                if (!NodeCache.nodeCache.containsKey(command[1])) {
                    send.append("未知的节点！");
                    break;
                }
                JSONObject nodeData = NodeReliable.getReliableData(command[1]);
                send.append("MossFrp 节点SLA统计数据");
                send.append("\r\n当日SLA：").append(String.format("%.2f",nodeData.getDouble("reliableDaily")*100)).append("%");
                send.append("\r\n累计SLA：").append(String.format("%.2f",nodeData.getDouble("reliableTotal")*100)).append("%");
                send.append("\r\n当日数据：").append(nodeData.getString("reliableDailyData").replace("|"," | "));
                send.append("\r\n累计数据：").append(nodeData.getString("reliableTotalData").replace("|"," | "));
                break;
        }
    }
}
