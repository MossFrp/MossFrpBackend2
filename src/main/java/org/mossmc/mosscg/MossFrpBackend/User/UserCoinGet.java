package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Enums;

public class UserCoinGet {
    public static int coinHave(String checkMode, String checkInfo, Enums.coinType coinType) {
        String targetInfo;
        switch (coinType) {
            case GOLD:
                targetInfo = "gold";
                break;
            case SILVER:
                targetInfo = "silver";
                break;
            default:
                return -1;
        }
        String get = UserInfo.getInfo(checkMode, checkInfo, targetInfo);
        if (get == null) {
            return -1;
        } else {
            try {
                return Integer.parseInt(get);
            } catch (Exception e) {
                return -1;
            }
        }
    }
}
