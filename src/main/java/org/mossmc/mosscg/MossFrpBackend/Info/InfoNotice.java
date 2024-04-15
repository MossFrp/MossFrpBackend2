package org.mossmc.mosscg.MossFrpBackend.Info;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class InfoNotice {
    public static Map<Integer,String> noticeTextCache = new HashMap<>();
    public static Map<Integer,String> noticeTitleCache = new HashMap<>();
    public static Map<Integer,Enums.noticeType> noticeTypeCache = new HashMap<>();
    public static List<Integer> displayList = new ArrayList<>();
    public static JSONObject displayJSON = new JSONObject();

    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(InfoNotice::updateVoid);
        thread.setName("nodeUpdateThread");
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void updateVoid() {
        while (true) {
            try {
                refreshNotice();
            } catch (Exception e) {
                sendException(e);
            } finally {
                try {
                    Thread.sleep(60*1000L);
                } catch (Exception e) {
                    sendException(e);
                }
            }
        }
    }

    public static void refreshNotice() throws Exception{
        Map<Integer,String> newTextCache = new HashMap<>();
        Map<Integer,String> newTitleCache = new HashMap<>();
        Map<Integer,Enums.noticeType> newNoticeTypeCache = new HashMap<>();
        List<Integer> topList = new ArrayList<>();
        List<Integer> superTopList = new ArrayList<>();
        List<Integer> normalList = new ArrayList<>();
        List<Integer> newNodeList = new ArrayList<>();
        ResultSet set = MysqlGetResult.getResultSet("select * from notice");
        while (set.next()) {
            int ID = set.getInt("ID");
            boolean top = Boolean.parseBoolean(set.getString("top"));
            boolean superTop = Boolean.parseBoolean(set.getString("superTop"));
            boolean display = Boolean.parseBoolean(set.getString("display"));
            String title = set.getString("title");
            String info = set.getString("info");
            //String prefix = "";
            if (display) {
                if (superTop) {
                    superTopList.add(ID);
                    newNoticeTypeCache.put(ID, Enums.noticeType.SUPER);
                    //prefix = "【置顶】";
                } else {
                    if (top) {
                        topList.add(ID);
                        newNoticeTypeCache.put(ID, Enums.noticeType.TOP);
                        //prefix = "[置顶]";
                    } else {
                        normalList.add(ID);
                        newNoticeTypeCache.put(ID, Enums.noticeType.NORMAL);
                        //prefix = "";
                    }
                }
            }
            newTitleCache.put(ID,title);
            //newTitleCache.put(ID,prefix+title);
            newTextCache.put(ID,info);

        }
        newNodeList.addAll(superTopList);
        newNodeList.addAll(topList);
        newNodeList.addAll(normalList);
        displayList.clear();
        displayList = newNodeList;
        noticeTextCache.clear();
        noticeTextCache = newTextCache;
        noticeTitleCache.clear();
        noticeTitleCache = newTitleCache;
        noticeTypeCache.clear();
        noticeTypeCache = newNoticeTypeCache;
        refreshJSON();
    }

    public static void refreshJSON() {
        JSONObject newJSON = new JSONObject();
        List<JSONObject> data = new ArrayList<>();
        newJSON.put("list",displayList);
        for (Integer ID : displayList) {
            JSONObject noticeJSON = new JSONObject();
            noticeJSON.put("ID", ID);
            noticeJSON.put("title", noticeTitleCache.get(ID));
            noticeJSON.put("text", noticeTextCache.get(ID));
            noticeJSON.put("type", noticeTypeCache.get(ID).name());
            data.add(noticeJSON);
        }
        newJSON.put("noticeData",data);
        displayJSON.clear();
        displayJSON = newJSON;
    }
}
