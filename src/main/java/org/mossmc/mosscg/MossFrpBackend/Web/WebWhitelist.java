package org.mossmc.mosscg.MossFrpBackend.Web;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class WebWhitelist {
    public static List<String> whitelistIPForever = new ArrayList<>();
    public static List<String> whitelistIPTemp = new ArrayList<>();
    public static FileWriter getWriter;

    public static void saveThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(WebWhitelist::autoSave);
        thread.setName("whitelistSaveThread");
        singleThreadExecutor.execute(thread::start);
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void autoSave() {
        while (true) {
            try {
                Thread.sleep(60*1000L);
                saveWhiteList();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void reloadWhiteList() {
        try {
            File target = new File("./MossFrp/Web/whitelist.yml");
            if (!target.exists()) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(target.toPath())));
            String read = reader.readLine();
            List<String> newWhiteList;
            if (read != null) {
                String[] IPs = read.split("\\|");
                newWhiteList = new ArrayList<>(Arrays.asList(IPs));
            } else {
                newWhiteList = new ArrayList<>();
            }
            whitelistIPForever.clear();
            whitelistIPTemp.clear();
            whitelistIPForever = newWhiteList;
            sendInfo("Web白名单读取成功！");
        } catch (Exception e) {
            sendException(e);
            sendWarn("Web白名单缓存读取失败！");
        }
    }

    public static void saveWhiteList() {
        try {
            getWriter =  new FileWriter("./MossFrp/Web/whitelist.yml",false);
            AtomicBoolean first = new AtomicBoolean(true);
            whitelistIPForever.forEach(IP -> {
                try {
                    if (!first.get()) {
                        getWriter.write("|");
                    }
                    first.set(false);
                    getWriter.write(IP);
                    getWriter.flush();
                } catch (IOException e) {
                    sendException(e);
                }
            });
            getWriter.close();
        } catch (Exception e) {
            sendException(e);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isWhitelist(String IP) {
        if (whitelistIPTemp.contains(IP)) return true;
        if (whitelistIPForever.contains(IP)) return true;
        return false;
    }

    public static void addWhiteList(String IP, Enums.whitelistType type) {
        if (type.equals(Enums.whitelistType.FOREVER)) {
            whitelistIPForever.add(IP);
        } else {
            whitelistIPTemp.add(IP);
        }
    }
}
