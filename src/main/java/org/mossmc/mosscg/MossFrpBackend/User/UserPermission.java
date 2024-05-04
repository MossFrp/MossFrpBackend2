package org.mossmc.mosscg.MossFrpBackend.User;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import java.util.HashMap;
import java.util.Map;

public class UserPermission {
    public static Map<String,Integer> permissionMap = new HashMap<>();

    public static boolean cachePermission = false;

    public static int getPermission(String uid) {
        if (cachePermission && permissionMap.containsKey(uid)) {
            return permissionMap.get(uid);
        } else {
            JSONObject userData = UserInfo.getUserInfo("userID",uid);
            if (userData == null) {
                return 999;
            }
            String level = userData.getString("level");
            int levelCode = BasicInfo.getUserLevelCode(level);
            permissionMap.put(uid,levelCode);
            return levelCode;
        }
    }
}
