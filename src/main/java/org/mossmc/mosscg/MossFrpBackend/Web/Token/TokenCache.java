package org.mossmc.mosscg.MossFrpBackend.Web.Token;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenCache {
    public static Map<String, Long> tokenTimeMap = new HashMap<>();
    public static Map<String, String> tokenIPMap = new HashMap<>();
    public static Map<String, String> tokenUserMap = new HashMap<>();
    public static Map<String, Enums.typeAPI> tokenTypeMap = new HashMap<>();

    public static Map<String, List<String>> IPTokenMap = new HashMap<>();

    public static void putIPTokenMap(String IP,String token) {
        if (!IPTokenMap.containsKey(IP)) {
            IPTokenMap.put(IP,new ArrayList<>());
        }
        IPTokenMap.get(IP).add(token);
    }

    public static void removeIPTokenMap(String token) {
        if (tokenIPMap.containsKey(token)) {
            String IP = tokenIPMap.get(token);
            IPTokenMap.get(IP).remove(token);
            if (IPTokenMap.get(IP).size() == 0) {
                IPTokenMap.remove(IP);
            }
        }
    }

    public static void removeUserToken(String userID) {
        List<String> userTokenList = new ArrayList<>();
        tokenUserMap.forEach((token, user) -> {
            if (user.equals(userID)) {
                userTokenList.add(token);
            }
        });
        userTokenList.forEach(TokenGet::removeToken);
    }
}
