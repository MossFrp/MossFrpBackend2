package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.File.FileClear;
import org.mossmc.mosscg.MossFrpBackend.File.FileFrp;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Web.Request.RequestAllCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getSystemType;
import static org.mossmc.mosscg.MossFrpBackend.File.FileCheck.checkFileExist;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class NodeProcess {
    public static boolean updating = true;
    public static boolean needUpdate = false;
    public static List<String> processIDList = new ArrayList<>();
    public static Map<String,Process> processIDMap = new HashMap<>();

    public static Map<String,Integer> portIDMap = new HashMap<>();
    public static Map<String,Integer> bandIDMap = new HashMap<>();
    public static Map<String,String> codeIDMap = new HashMap<>();

    public static void runThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(NodeProcess::updateVoid);
        thread.setName("nodeUpdateProcessThread");
        updating = false;
        singleThreadExecutor.execute(thread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void updateVoid() {
        while (true) {
            try {
                updateProcess();
            } catch (Exception e) {
                sendException(e);
            } finally {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    sendException(e);
                }
            }
        }
    }

    public synchronized static void updateProcess() {
        if (updating) {
            return;
        }
        try {
            JSONObject processData = NodeAPIConnection.getReturnData(RequestAllCode.getRequest());
            if (processData == null) {
                needUpdate = true;
                return;
            }
            needUpdate = false;
            updating = true;
;
            List<String> cacheID = new ArrayList<>();
            processData.forEach((number, codeDataString) -> {
                try {
                    if (!number.equals("status") && !number.equals("time")) {
                        JSONObject codeData = JSON.parseObject(codeDataString.toString());
                        String ID = codeData.getString("ID");
                        if (!processIDList.contains(ID)) {
                            runFrp(codeData);
                        } else {
                            if (!portIDMap.get(ID).equals(codeData.getInteger("port"))) {
                                stopFrp(ID);
                                runFrp(codeData);
                            }
                            if (!codeIDMap.get(ID).equals(codeData.getString("code"))) {
                                stopFrp(ID);
                                runFrp(codeData);
                            }
                            if (!bandIDMap.get(ID).equals(codeData.getInteger("band"))) {
                                NodeBandLimit.removeLimit(ID);
                                NodeBandLimit.setLimit(codeData.getInteger("ID"),codeData.getInteger("band"),codeData.getInteger("band"));
                                bandIDMap.remove(ID);
                                bandIDMap.put(ID,codeData.getInteger("band"));
                            }

                        }
                        cacheID.add(ID);
                    }
                } catch (Exception e) {
                    sendException(e);
                }
            });

            List<String> cacheLocalID = new ArrayList<>(processIDList);
            cacheID.forEach(cacheLocalID::remove);
            cacheLocalID.forEach(NodeProcess::stopFrp);

        } catch (Exception e) {
            sendException(e);
        } finally {
            updating = false;
        }
    }

    public static void clearProcess() {
        File file = new File("./MossFrp/Frps");
        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }
        Process kill;
        for (File directory : fileList) {
            if (!directory.isDirectory()) {
                continue;
            }
            if (directory.getName().equals("files")) {
                continue;
            }
            try {
                String name = directory.getName().replace("frp", "frps");
                if (getSystemType == Enums.systemType.WINDOWS) {
                    kill = Runtime.getRuntime().exec("taskkill /im " + name + ".exe /f");
                } else {
                    kill = Runtime.getRuntime().exec("pkill " + name);
                }
                BufferedReader output = new BufferedReader(new InputStreamReader(kill.getInputStream()));
                output.readLine();
                output.close();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void runFrp(JSONObject data) {
        LoggerSender.sendInfo("正在启动Frp："+ data.getString("ID"));

        int band = data.getInteger("band");
        int port = data.getInteger("port");
        String ID = data.getString("ID");
        String code = data.getString("code");

        try {
            Process process = null;

            FileFrp.copyCore(ID);
            FileFrp.writeConfig(ID, String.valueOf(port),code);
            NodeBandLimit.setLimit(Integer.parseInt(ID), band,port);

            switch (getSystemType) {
                case WINDOWS:
                    process = Runtime.getRuntime().exec("./MossFrp/Frps/frp_"+ID+"/frps_"+ID+".exe -c ./MossFrp/Frps/frp_"+ID+"/frps.ini");
                    break;
                case LINUX:
                    process = Runtime.getRuntime().exec("./MossFrp/Frps/frp_"+ID+"/frps_"+ID+" -c ./MossFrp/Frps/frp_"+ID+"/frps.ini");
                    break;
                default:
                    break;
            }

            processIDList.add(ID);
            processIDMap.put(ID,process);
            portIDMap.put(ID,port);
            bandIDMap.put(ID,band);
            codeIDMap.put(ID,code);
        } catch (Exception e) {
            sendException(e);
        }

        LoggerSender.sendInfo("完成启动Frp："+ data.getString("ID"));
    }

    public static void stopFrp(String ID) {
        LoggerSender.sendInfo("正在停止Frp："+ ID);
        if (processIDMap.containsKey(ID)) {
            processIDMap.get(ID).destroy();
        }
        NodeBandLimit.removeLimit(ID);
        processIDMap.remove(ID);
        processIDList.remove(ID);
        portIDMap.remove(ID);
        bandIDMap.remove(ID);
        codeIDMap.remove(ID);
        LoggerSender.sendInfo("完成停止Frp："+ ID);
    }
}
