package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class NodeAPIConnection {
    public static String centerAPIAddress;

    public static void initAPIInfo() {
        try {
            centerAPIAddress = BasicInfo.getConfig("centerURL");
            System.setProperty("sun.net.client.defaultConnectTimeout", "3000");
            System.setProperty("sun.net.client.defaultReadTimeout", "3000");
            System.setProperty("http.keepAlive", "false");
        } catch (Exception e) {
            sendException(e);
            sendError("无法加载主控API链接");
        }
    }

    public static JSONObject getReturnData(JSONObject data) {
        try {
            StringBuilder target = new StringBuilder(centerAPIAddress + "?");
            AtomicBoolean spilt = new AtomicBoolean(false);
            data.forEach((key, value) -> {
                if (spilt.get()) {
                    target.append("&");
                }
                target.append(key).append("=").append(value.toString());
                spilt.set(true);
            });
            URL targetURL = new URL(target.toString());
            HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("User-Agent", "MossFrpNode/"+BasicInfo.getVersion);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder readInfo = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                readInfo.append(inputLine);
            }
            reader.close();
            sendAPI("["+data+"]["+readInfo+"]", Enums.typeAPI.NODE);
            return JSONObject.parseObject(readInfo.toString());
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }
}
