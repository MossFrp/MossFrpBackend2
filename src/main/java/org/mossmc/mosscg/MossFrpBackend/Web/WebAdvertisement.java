package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlUpdate;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

//status: check run stop reject
public class WebAdvertisement {
    public static Map<Integer,Integer> weightCache = new HashMap<>();
    public static Map<Integer,String> linkCache = new HashMap<>();
    public static Map<Integer,String> jumpCache = new HashMap<>();
    public static Map<Integer,Integer> shownCache = new HashMap<>();
    public static List<Integer> adsList = new ArrayList<>();

    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(WebAdvertisement::updateVoid);
        thread.setName("adsUpdateThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void updateVoid() {
        while (true) {
            try {
                updateAll();
            } catch (Exception e) {
                sendException(e);
            } finally {
                try {
                    Thread.sleep(1000L*60);
                } catch (Exception e) {
                    sendException(e);
                }
            }
        }
    }

    public static void updateAll() {
        checkStop();
        updateShown();
        updateCache();
        updateWeight();
    }

    public static void addAD(int day,int weight,String userID,String link,String jump) {
        try {
            long start = System.currentTimeMillis();
            long stop = start+day*24*60*60*1000L -1;
            JSONObject json = new JSONObject();
            json.put("userID",userID);
            json.put("start",start);
            json.put("stop",stop);
            json.put("status","check");
            json.put("link",link);
            json.put("jump",jump);
            json.put("weight",weight);
            MysqlInsert.insert("ads",json);
            sendInfo("广告："+link+"已加入审核列表！");
            BotQQ.sendAdminMessage("有新的广告审核！请及时过审！\r\n提交用户："+userID);
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static List<JSONObject> getUserAD(String userID) {
        try {
            List<JSONObject> result = new ArrayList<>();
            String sql;
            sql = "select * from ads where userID="+userID;
            ResultSet set = MysqlGetResult.getResultSet(sql);
            while (set.next()) {
                if (set.getString("status").equals("stop") || set.getString("status").equals("reject")) continue;
                JSONObject data = new JSONObject();
                data.put("ID",set.getString("ID"));
                data.put("userID",set.getString("userID"));
                data.put("status",set.getString("status"));
                data.put("shown",set.getString("shown"));
                data.put("start",set.getString("start"));
                data.put("stop",set.getString("stop"));
                data.put("weight",set.getString("weight"));
                data.put("jump",set.getString("jump"));
                data.put("link",set.getString("link"));
                result.add(data);
            }
            return result;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static List<JSONObject> getAllAD() {
        try {
            List<JSONObject> result = new ArrayList<>();
            String sql;
            sql = "select * from ads where status='run'";
            ResultSet set = MysqlGetResult.getResultSet(sql);
            while (set.next()) {
                JSONObject data = new JSONObject();
                data.put("ID",set.getString("ID"));
                data.put("userID",set.getString("userID"));
                data.put("status",set.getString("status"));
                data.put("shown",set.getString("shown"));
                data.put("start",set.getString("start"));
                data.put("stop",set.getString("stop"));
                data.put("weight",set.getString("weight"));
                data.put("jump",set.getString("jump"));
                data.put("link",set.getString("link"));
                result.add(data);
            }
            return result;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static List<JSONObject> getCheckAD() {
        try {
            List<JSONObject> result = new ArrayList<>();
            String sql;
            sql = "select * from ads where status='check'";
            ResultSet set = MysqlGetResult.getResultSet(sql);
            while (set.next()) {
                JSONObject data = new JSONObject();
                data.put("ID",set.getString("ID"));
                data.put("userID",set.getString("userID"));
                data.put("status",set.getString("status"));
                data.put("shown",set.getString("shown"));
                data.put("start",set.getString("start"));
                data.put("stop",set.getString("stop"));
                data.put("weight",set.getString("weight"));
                data.put("jump",set.getString("jump"));
                data.put("link",set.getString("link"));
                result.add(data);
            }
            return result;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static String getADString(JSONObject data,String type) {
        StringBuilder send = new StringBuilder();
        send.append("\r\n");
        switch (data.getString("status")) {
            case "check":
                send.append("[审核中]");
                break;
            case "run":
                send.append("[展现中]");
                break;
            case "reject":
                send.append("[未过审]");
                break;
            case "stop":
                send.append("[已到期]");
                break;
            default:
                send.append("[未知]");
                break;
        }
        send.append("[#").append(data.getString("ID")).append("]");
        int remainDay = (int) ((data.getLong("stop")-System.currentTimeMillis())/(24*60*60*1000L)) +1;
        if (remainDay <= 0) remainDay = 0;
        send.append("\r\n剩余天数：").append(remainDay);
        send.append("\r\n展现权重：").append(data.getString("weight"));
        send.append("\r\n累积展现：").append(data.getString("shown"));
        if (type.equals("full")) {
            send.append("\r\n归属用户：").append(data.getString("userID"));
            send.append("\r\n图片链接：").append(data.getString("link"));
            send.append("\r\n跳转链接：").append(data.getString("jump"));
        }
        return send.toString();
    }

    public static JSONObject getADData(String key,String value) {
        try {
            String sql;
            sql = "select * from ads where "+key+"="+value;
            ResultSet set = MysqlGetResult.getResultSet(sql);
            JSONObject data = new JSONObject();
            set.next();
            data.put("ID",set.getString("ID"));
            data.put("userID",set.getString("userID"));
            data.put("status",set.getString("status"));
            data.put("shown",set.getString("shown"));
            data.put("start",set.getString("start"));
            data.put("stop",set.getString("stop"));
            data.put("weight",set.getString("weight"));
            data.put("jump",set.getString("jump"));
            data.put("link",set.getString("link"));
            return data;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static void checkPass(int ID) {
        try {
            String sql;
            sql = "select * from ads where ID="+ID;
            ResultSet set = MysqlGetResult.getResultSet(sql);
            set.next();
            long start = set.getLong("start");
            long stop = set.getLong("stop");
            long newStop = stop+(System.currentTimeMillis()-start);
            sql = "update ads set stop="+newStop+" WHERE ID="+ID;
            MysqlUpdate.execute(sql);
            sql = "update ads set status='run' WHERE ID="+ID;
            MysqlUpdate.execute(sql);
            String email = UserInfo.getInfo("userID",set.getString("userID"),"email");
            String day = String.valueOf((int)((stop-start)/(24*60*60*1000L))+1);
            String weight = set.getString("weight");
            String link = set.getString("link")+" -> "+set.getString("jump");
            MailSend.sendADPass(email,String.valueOf(ID),day,weight,link);
            sendInfo("广告ID："+ID+"已通过审核！");
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void checkReject(int ID,String reason) {
        try {
            String sql;
            sql = "update ads set status='reject' WHERE ID="+ID;
            MysqlUpdate.execute(sql);
            sql = "select * from ads where ID="+ID;
            ResultSet set = MysqlGetResult.getResultSet(sql);
            set.next();
            String email = UserInfo.getInfo("userID",set.getString("userID"),"email");
            MailSend.sendADReject(email,String.valueOf(ID),reason);
            sendInfo("广告ID："+ID+"未通过审核！");
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void checkStop() {
        try {
            String sql;
            sql = "select * from ads where status='run'";
            ResultSet set = MysqlGetResult.getResultSet(sql);
            while (set.next()) {
                long stop = set.getLong("stop");
                if (stop < System.currentTimeMillis()) {
                    String ID = set.getString("ID");
                    sql = "update ads set status='stop' WHERE ID="+ID;
                    MysqlUpdate.execute(sql);
                    sql = "select * from ads where ID="+ID;
                    ResultSet setUser = MysqlGetResult.getResultSet(sql);
                    setUser.next();
                    String email = UserInfo.getInfo("userID",setUser.getString("userID"),"email");
                    MailSend.sendADOutdated(email,String.valueOf(ID),set.getString("shown"));
                    sendInfo("广告ID："+ID+"已到期下架！");
                }
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void updateCache() {
        try {
            String sql;
            sql = "select * from ads where status='run'";
            ResultSet set = MysqlGetResult.getResultSet(sql);
            Map<Integer,Integer> newWeightCache = new HashMap<>();
            Map<Integer,String> newLinkCache = new HashMap<>();
            Map<Integer,String> newJumpCache = new HashMap<>();
            List<Integer> newADsList = new ArrayList<>();
            while (set.next()) {
                int ID = set.getInt("ID");
                int weight = set.getInt("weight");
                String link = set.getString("link");
                String jump = set.getString("jump");
                newADsList.add(ID);
                newWeightCache.put(ID,weight);
                newLinkCache.put(ID,link);
                newJumpCache.put(ID,jump);
            }
            weightCache.clear();
            weightCache = newWeightCache;
            linkCache.clear();
            linkCache = newLinkCache;
            jumpCache.clear();
            jumpCache = newJumpCache;
            adsList.clear();
            adsList = newADsList;
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static int weightTotal = 0;
    public static Random random = new Random();
    public static Map<Integer,Integer> weightIDCache = new HashMap<>();
    public static void updateWeight() {
        try {
            Map<Integer,Integer> newWeightIDCache = new HashMap<>();
            int loc = 0;
            int total = 0;
            for (Integer ID : adsList) {
                int weight = weightCache.get(ID);
                total += weight;
                for (int i = 0; i < weight; i++) {
                    newWeightIDCache.put(loc,ID);
                    loc++;
                }
            }
            weightIDCache.clear();
            weightIDCache = newWeightIDCache;
            weightTotal = total;
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static JSONObject getAD() {
        int loc = random.nextInt(weightTotal);
        int ID = weightIDCache.get(loc);
        addShown(ID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID",ID);
        jsonObject.put("link",linkCache.get(ID));
        jsonObject.put("jump",jumpCache.get(ID));
        return jsonObject;
    }

    public static void addShown(int ID) {
        try {
            if (!shownCache.containsKey(ID)) {
                shownCache.put(ID,0);
            }
            shownCache.replace(ID,shownCache.get(ID)+1);
        } catch (Exception e) {
            sendException(e);
        }

    }

    public static void updateShown() {
        Map<Integer,Integer> cache = new HashMap<>(shownCache);
        shownCache.clear();
        cache.forEach((ID, times) -> {
            try {
                String sql;
                sql = "select * from ads where ID="+ID;
                ResultSet set = MysqlGetResult.getResultSet(sql);
                set.next();
                int newShow = set.getInt("shown")+times;
                sql = "update ads set shown='"+newShow+"' WHERE ID="+ID;
                MysqlUpdate.execute(sql);
            } catch (Exception e) {
                sendException(e);
            }
        });
        cache.clear();
    }
}
