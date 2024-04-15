package org.mossmc.mosscg.MossFrpBackend.Web;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class WebBlacklist {
    public static int maxBadRequestPerMinute = 500;
    public static int IPMaxRequestPerMinute = 1000;

    public static ConcurrentHashMap<String,Long> blacklistIPMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String,Integer> badRequestCountMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,Integer> requestCountMap = new ConcurrentHashMap<>();

    public static void checkThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(WebBlacklist::autoCheck);
        thread.setName("blacklistCheckThread");
        singleThreadExecutor.execute(thread::start);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void autoCheck() {
        int n = 1;
        while (true) {
            try {
                Thread.sleep(1000);
                List<String> removeIP = new ArrayList<>();
                blacklistIPMap.forEach((IP, time) -> {
                    if (time < System.currentTimeMillis()) {
                        removeIP.add(IP);
                    }
                });
                removeIP.forEach(IP -> blacklistIPMap.remove(IP));
                if (n >= 60) {
                    n = 0;
                    requestCountMap.forEach((IP, time) -> {
                        if (time >= IPMaxRequestPerMinute) {
                            addBlacklistIP("Request too fast!",IP,60);
                            requestCountMap.replace(IP,0);
                        }
                    });
                    requestCountMap.clear();
                }
                n++;
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void addBlacklistIP(String reason,String IP,int seconds) {
        if (WebWhitelist.isWhitelist(IP)) return;
        if (blacklistIPMap.containsKey(IP)) {
            int newSecond = seconds+(int)((blacklistIPMap.get(IP)-System.currentTimeMillis())/1000);
            String info = "已自动延长拉黑至"+newSecond+"S来自IP："+IP+"的请求！原因："+reason;
            blacklistIPMap.replace(IP,System.currentTimeMillis()+1000L*newSecond);
            sendWarn(info);
        } else {
            blacklistIPMap.put(IP,1000L*seconds+System.currentTimeMillis());
            String info = "已自动拉黑"+seconds+"S来自IP："+IP+"的请求！原因："+reason;
            sendWarn(info);
            BotQQ.sendAdminMessage(info);
        }
    }

    public static synchronized void addBadRequestCount(String IP,int count) {
        if (!badRequestCountMap.containsKey(IP)) {
            badRequestCountMap.put(IP,count);
        } else {
            badRequestCountMap.replace(IP,badRequestCountMap.get(IP)+count);
            if (badRequestCountMap.get(IP) >= maxBadRequestPerMinute) {
                addBlacklistIP("Too many bad requests!",IP,60);
                badRequestCountMap.replace(IP,badRequestCountMap.get(IP)-maxBadRequestPerMinute);
            }
        }
    }

    public static synchronized void addRequestCount(String IP) {
        if (!requestCountMap.containsKey(IP)) {
            requestCountMap.put(IP,1);
        } else {
            requestCountMap.replace(IP,requestCountMap.get(IP)+1);
            if (requestCountMap.get(IP) >= IPMaxRequestPerMinute) {
                addBlacklistIP("Request too fast!",IP,60);
                requestCountMap.replace(IP,0);
            }
        }
    }

    public static boolean checkBlacklist(String IP) {
        return blacklistIPMap.containsKey(IP);
    }
}
