package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlUpdate;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeRemain;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;

import java.sql.ResultSet;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class CodeInfo {
    public static JSONObject getNodeCodeInfo(String node) {
        JSONObject data = new JSONObject();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where node=? and status='run'",node);
            int count = 0;
            while (set.next()) {
                count++;
                JSONObject info = new JSONObject();
                info.put("ID", set.getString("ID"));
                info.put("node", set.getString("node"));
                info.put("number", set.getString("number"));
                info.put("band", set.getString("band"));
                info.put("port", set.getString("port"));
                info.put("code", set.getString("code"));
                data.put(String.valueOf(count), info);
            }
            return data;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static JSONObject getUserCodeInfo(String user) {
        JSONObject data = new JSONObject();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where user=?",user);
            int count = 0;
            while (set.next()) {
                count++;
                JSONObject info = new JSONObject();
                info.put("ID", set.getString("ID"));
                info.put("node", set.getString("node"));
                info.put("number", set.getString("number"));
                info.put("user", set.getString("user"));
                info.put("status", set.getString("status"));
                info.put("activity", set.getString("activity"));
                info.put("warning", set.getString("warning"));
                info.put("band", set.getString("band"));
                info.put("port", set.getString("port"));
                info.put("code", set.getString("code"));
                info.put("stop", set.getString("stop"));
                info.put("start", set.getString("start"));
                data.put(String.valueOf(count), info);
            }
            return data;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static JSONObject getCodeInfo(String key,String value) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where "+key+"=?",value);
            JSONObject info = new JSONObject();
            if (set.next()) {
                info.put("ID", set.getString("ID"));
                info.put("node", set.getString("node"));
                info.put("number", set.getString("number"));
                info.put("user", set.getString("user"));
                info.put("status", set.getString("status"));
                info.put("activity", set.getString("activity"));
                info.put("warning", set.getString("warning"));
                info.put("band", set.getString("band"));
                info.put("port", set.getString("port"));
                info.put("code", set.getString("code"));
                info.put("stop", set.getString("stop"));
                info.put("start", set.getString("start"));
            }
            return info;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static JSONObject getCodeInfoByNodeNumber(String node,String number) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where node=? and number=?",node,number);
            JSONObject info = new JSONObject();
            if (set.next()) {
                info.put("ID", set.getString("ID"));
                info.put("node", set.getString("node"));
                info.put("number", set.getString("number"));
                info.put("user", set.getString("user"));
                info.put("status", set.getString("status"));
                info.put("activity", set.getString("activity"));
                info.put("warning", set.getString("warning"));
                info.put("band", set.getString("band"));
                info.put("port", set.getString("port"));
                info.put("code", set.getString("code"));
                info.put("stop", set.getString("stop"));
                info.put("start", set.getString("start"));
            } else {
                return null;
            }
            return info;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    @SuppressWarnings("All")
    //mode有三个参数all full light
    public static String getUserAllAsString(long uid,String mode) {
        StringBuilder message = new StringBuilder();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code WHERE user=?", String.valueOf(uid));
            message.append("MossFrp 穿透码列表");
            boolean haveCode = false;
            boolean haveNotCreate = false;
            while (set.next()) {
                haveCode = true;
                String node = set.getString("node");
                String code = set.getString("code");
                String band = set.getString("band");
                String createTime = TimeString.getTimeString(Enums.timeStringType.FULL,set.getLong("start"));
                String deadlineTime = TimeString.getTimeString(Enums.timeStringType.FULL,set.getLong("stop"));
                String status = BasicInfo.getCodeStatusName(set.getString("status"));
                String port = set.getString("port");
                String number = set.getString("number");
                String remain = TimeRemain.dateRemainString(node,number);
                //理论上，不会出现创建中了
                //但是还是保留
                if (port.equals("0")) {
                    haveNotCreate = true;
                    message.append("\r\n[创建中][" + node + " " + number + "]\r\n");
                    message.append("带宽：" + band + "Mbps\r\n");
                    message.append("剩余时长：" + remain);
                    continue;
                }
                if (mode.equals("all")) {
                    message.append("\r\n[" + status + "][" + node + " " + number + "]\r\n");
                    message.append("带宽：" + band + "Mbps\r\n");
                    message.append("端口：" + port + "（+9）\r\n");
                    if (set.getString("status").equals("run")) {
                        message.append("穿透码：" + code + "\r\n");
                    }
                    message.append("创建时间：" + createTime + "\r\n");
                    message.append("到期时间：" + deadlineTime + "\r\n");
                    message.append("剩余时长：" + remain);
                    continue;
                }
                if (mode.equals("full")) {
                    message.append("\r\n[" + status + "][" + node + " " + number + "]\r\n");
                    message.append("带宽：" + band + "Mbps\r\n");
                    message.append("端口：" + port + "（+9）\r\n");
                    if (set.getString("status").equals("run")) {
                        message.append("穿透码：" + code + "\r\n");
                    }
                    message.append("剩余时长：" + remain);
                } else {
                    message.append("\r\n["+status+"]["+node+" "+number+"]\r\n");
                    message.append("带宽："+band+"Mbps\r\n");
                    message.append("剩余时长：" + remain);
                }
            }
            if (!haveCode) {
                message.append("\r\n你还没有穿透码哦，快用指令创建一个吧~");
            }
            if (haveNotCreate) {
                message.append("\r\n若穿透码长时间未完成创建，请尝试使用 #刷新穿透码 指令来解决并私聊墨守报销~");
            }
            if (!mode.equals("full")&&!mode.equals("all")) {
                message.append("\r\n私聊使用此指令才可以获取穿透码及其全部参数哦~");
            }
        } catch (Exception e) {
            sendException(e);
            return "查询失败！数据库错误！";
        }
        return message.toString();
    }

    public static void updateCodeInfo(String key,String value,String updateKey,String updateValue) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from code where "+updateKey+"=?",updateValue);
            if (set.next()) {
                NodeCache.nodeUpdate.add(set.getString("node"));
            }
            String sql = "update code set "+updateKey+"='"+updateValue+ "' WHERE "+key+"='"+value+"'";
            MysqlUpdate.execute(sql);
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static int getUserCodeCount(String userID) throws Exception{
        ResultSet set = MysqlGetResult.getResultSet("select count(*) from code where user=?",userID);
        set.next();
        return set.getInt(1);
    }
}
