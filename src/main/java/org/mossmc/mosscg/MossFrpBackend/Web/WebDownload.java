package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class WebDownload {
    public static JSONObject downloadData = new JSONObject();

    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(WebDownload::autoUpdate);
        thread.setName("downloadUpdateThread");
        singleThreadExecutor.execute(thread::start);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void autoUpdate() {
        while (true) {
            try {
                reloadDownloadData();
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

    public static void reloadDownloadData() {
        try {
            File target = new File("./MossFrp/Web/download.json");
            if (!target.exists()) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(target.toPath()), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String read;
            while ((read = reader.readLine())!= null) {
                builder.append(read);
            }
            JSONObject readData = JSONObject.parseObject(builder.toString(), Feature.OrderedField);
            JSONObject newDownloadData = new JSONObject();
            readData.forEach((key, object) -> {
                JSONObject dataTemp = JSONObject.parseObject(object.toString(), Feature.OrderedField);
                JSONObject dataNew = new JSONObject(new LinkedHashMap<>());
                dataNew.put("name",dataTemp.getString("name"));
                dataNew.put("version",dataTemp.getString("version"));
                dataNew.put("description",dataTemp.getString("description"));
                dataNew.put("update",dataTemp.getString("update"));
                JSONObject linkJSON = new JSONObject();
                dataTemp.getJSONObject("links").forEach((linkKey, linkObject) -> {
                    JSONObject linkData = JSONObject.parseObject(linkObject.toString(), Feature.OrderedField);
                    linkJSON.put(linkKey,linkData);
                });
                dataNew.put("links",linkJSON);
                newDownloadData.put(key,dataNew);
            });
            downloadData.clear();
            downloadData = newDownloadData;
        } catch (Exception e) {
            sendException(e);
        }
    }
}
