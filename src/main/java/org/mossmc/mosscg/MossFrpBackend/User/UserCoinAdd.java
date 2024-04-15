package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getCoinTypeName;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class UserCoinAdd {
    public static String coinAdd(Enums.coinType coinType, Integer amount, String userInfo, String userInfoType) {
        String userInfoTypeUse = userInfoType;
        if (userInfoType.equals("uid")) {
            userInfoTypeUse = "userID";
        }
        int coinHave = UserCoinGet.coinHave(userInfoTypeUse,userInfo,coinType);
        if (coinHave == -1) {
            return "交易失败，用户数据查询失败！";
        }
        int coinNew = coinHave+amount;
        UserInfo.updateInfo(userInfoTypeUse,userInfo,coinType.name().toLowerCase(),String.valueOf(coinNew));
        sendInfo( "成功给 " + userInfo + " [" + userInfoType + "] " + "添加了 " + amount + " " + getCoinTypeName(coinType) + "!");
        sendInfo( "当前数量：" + coinNew);
        return "交易成功，当前"+getCoinTypeName(coinType)+"数量为"+coinNew;
    }
}
