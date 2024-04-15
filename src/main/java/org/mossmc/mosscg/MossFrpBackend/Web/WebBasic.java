package org.mossmc.mosscg.MossFrpBackend.Web;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.File.FileCheck;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class WebBasic {
    public static boolean displayNodeAPI = false;
    public static boolean displayClientAPI = true;

    public static void checkWebFile() {
        FileCheck.checkDirExist("./MossFrp/Web");
        FileCheck.checkFileExist("./MossFrp/Web/shop.json","shop.json");
        FileCheck.checkFileExist("./MossFrp/Web/download.json","download.json");
    }

    public static String getRemoteIP(HttpExchange exchange) {
        String remote;
        if (exchange.getRequestHeaders().containsKey("X-Real-IP")) {
            remote = getIPString(exchange.getRequestHeaders().get("X-Real-IP"));
        } else {
            if (exchange.getRequestHeaders().containsKey("X-forwarded-for")) {
                remote = getIPString(exchange.getRequestHeaders().get("X-forwarded-for"));
            } else {
                remote = exchange.getRemoteAddress().getHostString();
            }
        }
        return remote;
    }

    public static String getIPString(List<String> list) {
        if (list.get(0).contains(",")) {
            String[] clearIPArray = list.get(0).split(",");
            List<String> realIPList = new ArrayList<>(Arrays.asList(clearIPArray));
            return realIPList.get(0);
        } else {
            return list.get(0);
        }
    }

    public static JSONObject loadRequestData(HttpExchange request) {
        //基础解码转JSON部分
        JSONObject jsonObject = new JSONObject();
        String[] cut = request.getRequestURI().toString().split("\\?");
        if (cut.length <= 1) {
            return jsonObject;
        }
        String requestData = cut[1];
        cut = requestData.split("&");
        for (String s : cut) {
            String[] cutData = s.split("=");
            if (cutData.length <= 1) {
                continue;
            }
            jsonObject.put(cutData[0], cutData[1]);
        }
        //post或非post处理
        JSONObject result = null;
        if (jsonObject.containsKey("void") && jsonObject.getString("void").equals("post")) {
            //post，读取数据
            try {
                InputStream inputStream = request.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder text = new StringBuilder();
                String read;
                while ((read = reader.readLine())!=null) {
                    text.append(read);
                }
                result = JSONObject.parseObject(text.toString());
            } catch (Exception e) {
                sendException(e);
            }
            if (result == null) {
                result = new JSONObject();
            }
        } else {
            //非post，解码中文
            result = new JSONObject();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value.toString().contains("%")) {
                    try {
                        result.put(key, URLDecoder.decode(value.toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        sendException(e);
                    }
                } else {
                    result.put(key, value);
                }
            }
        }
        return result;
    }
}
