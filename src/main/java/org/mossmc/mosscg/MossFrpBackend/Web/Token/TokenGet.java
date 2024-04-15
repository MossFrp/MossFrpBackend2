package org.mossmc.mosscg.MossFrpBackend.Web.Token;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.util.Random;
import java.util.UUID;

public class TokenGet {
    public static String getNewToken() {
        return UUID.randomUUID().toString();
    }

    public static String registerToken(String IP, Enums.typeAPI typeAPI, String userID) {
        String token = getNewToken();
        TokenCache.tokenTimeMap.put(token,System.currentTimeMillis());
        TokenCache.tokenIPMap.put(token,IP);
        TokenCache.tokenUserMap.put(token,userID);
        TokenCache.tokenTypeMap.put(token, typeAPI);
        TokenCache.putIPTokenMap(IP,token);
        return token;
    }

    public static void removeToken(String token) {
        TokenCache.removeIPTokenMap(token);
        TokenCache.tokenTimeMap.remove(token);
        TokenCache.tokenIPMap.remove(token);
        TokenCache.tokenTypeMap.remove(token);
        TokenCache.tokenUserMap.remove(token);
    }
}
