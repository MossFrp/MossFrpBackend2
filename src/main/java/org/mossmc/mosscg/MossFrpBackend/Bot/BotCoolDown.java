package org.mossmc.mosscg.MossFrpBackend.Bot;

import java.util.HashMap;
import java.util.Map;

public class BotCoolDown {
    public static Map<String,Long> coolDownMap = new HashMap<>();
    public static Boolean coolDown(String mode,String number,String control) {
        String requestInfo = mode+"-"+number+"-"+control;
        if (!coolDownMap.containsKey(requestInfo)) {
            return false;
        }
        if (coolDownMap.get(requestInfo) <= System.currentTimeMillis()) {
            coolDownMap.remove(requestInfo);
            return false;
        }
        return true;
    }
    public static void setCoolDown(String mode,String number,Integer second,String control) {
        String requestInfo = mode+"-"+number+"-"+control;
        if (coolDownMap.containsKey(requestInfo)) {
            coolDownMap.replace(requestInfo, System.currentTimeMillis() + second * 1000);
        } else {
            coolDownMap.put(requestInfo, System.currentTimeMillis() + second * 1000);
        }

    }
}
