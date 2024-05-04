package org.mossmc.mosscg.MossFrpBackend.Web.Token;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserIP;
import org.mossmc.mosscg.MossFrpBackend.Web.WebAdvertisement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class TokenCheck {
    public static boolean checkNode(JSONObject data) {
        String node = data.getString("node");
        String auth = data.getString("auth");
        //参数检测
        if (node == null || auth == null) {
            return false;
        }
        //节点存在性检测
        if (!NodeCache.nodeCache.containsKey(node)) {
            return false;
        }
        //密钥检测
        if (!NodeCache.nodeCache.get(node).get("password").equals(auth)) {
            return false;
        }
        return true;
    }

    public static boolean checkClient(String token, String IP) {
        //是否包含Token检测
        if (!TokenCache.tokenTimeMap.containsKey(token)) {
            return false;
        }
        //Token是否超时检测
        if (!timeCheck(token)) {
            return false;
        }
        //IP是否对应检测
        if (!TokenCache.tokenIPMap.get(token).equals(IP)) {
            return false;
        }
        UserIP.logWebIP(token,IP);
        TokenCache.tokenTimeMap.replace(token,System.currentTimeMillis());
        return true;
    }

    public static boolean timeCheck(String token) {
        return TokenCache.tokenTimeMap.get(token)+1000L*60*30 >= System.currentTimeMillis();
    }
}
