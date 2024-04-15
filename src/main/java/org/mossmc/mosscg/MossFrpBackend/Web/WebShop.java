package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class WebShop {
    public static List<JSONObject> shopData = new ArrayList<>();

    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(WebShop::autoUpdate);
        thread.setName("shopUpdateThread");
        singleThreadExecutor.execute(thread::start);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void autoUpdate() {
        while (true) {
            try {
                reloadShopData();
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

    public static void reloadShopData() {
        try {
            File target = new File("./MossFrp/Web/shop.json");
            if (!target.exists()) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(target.toPath()), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String read;
            while ((read = reader.readLine())!= null) {
                builder.append(read);
            }
            JSONObject readData = JSONObject.parseObject(builder.toString(), Feature.OrderedField);
            List<JSONObject> newDataList = new ArrayList<>();
            readData.forEach((key, object) -> {
                JSONObject data = JSONObject.parseObject(object.toString());
                newDataList.add(data);
            });
            shopData.clear();
            shopData = newDataList;
        } catch (Exception e) {
            sendException(e);
        }
    }
}
