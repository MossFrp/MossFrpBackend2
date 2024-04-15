package org.mossmc.mosscg.MossFrpBackend.Bot;

import java.util.HashMap;
import java.util.Map;

public class BotConfirm {
    public static Map<String,Map<String,String>> confirmMap = new HashMap<>();
    public static void removeConfirmMap(String mode,String number) {
        confirmMap.remove(mode+"-"+number);
    }

    public static void inputConfirmMap(String mode,String number,String key,String value) {
        if (!confirmMap.containsKey(mode+"-"+number)) {
            confirmMap.put(mode+"-"+number,new HashMap<>());
        }
        confirmMap.get(mode+"-"+number).put(key, value);
    }

    public static void updateConfirmMap(String mode,String number,String key,String value) {
        confirmMap.get(mode+"-"+number).replace(key, value);
    }

    public static Map<String,String> getConfirmMap(String mode,String number) {
        return confirmMap.get(mode+"-"+number);
    }

    public static boolean containConfirmMap(String mode,String number) {
        return confirmMap.containsKey(mode + "-" + number);
    }
}
