package org.mossmc.mosscg.MossFrpBackend.Info;

import com.alibaba.fastjson.JSONObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfoCheck {
    public static String getBadString = ";|!|*|$|#|<|>";

    @SuppressWarnings("RedundantIfStatement")
    public static boolean checkString(String input) {
        String[] cut = getBadString.split("\\|");
        input = input.toLowerCase();
        for (String part : cut) {
            if (input.contains(part)) {
                return false;
            }
        }
        if (input.contains(" ")) {
            return false;
        }
        return true;
    }

    public static boolean checkData(JSONObject data) {
        AtomicBoolean result = new AtomicBoolean(true);
        data.forEach((key, info) -> {
            if (!key.equals("password")) {
                if (!checkString(info.toString())) {
                    result.set(false);
                }
            }
        });
        return result.get();
    }
}
