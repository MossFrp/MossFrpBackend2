package org.mossmc.mosscg.MossFrpBackend.User;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getCoinTypeName;
import static org.mossmc.mosscg.MossFrpBackend.Enums.coinType;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class UserCoinTake {
    public synchronized static String coinTake(coinType coinTypeRequest, Integer need, String userInfo, String userInfoType, String provider) {
        int gold = UserCoinGet.coinHave(userInfoType,userInfo, coinType.GOLD);
        int silver = UserCoinGet.coinHave(userInfoType,userInfo, coinType.SILVER);
        switch (coinTypeRequest) {
            case SILVER:
                if (need > silver) {
                    return "货币不足！需要银币"+need+",实际拥有银币"+silver;
                }
                coinRemove(coinType.SILVER,need,userInfo,userInfoType);
                if (provider != null) {
                    UserCoinAdd.coinAdd(coinType.SILVER,need*3/100,provider,"userID");
                }
                break;
            case GOLD:
                if (need > gold) {
                    return "货币不足！需要金币"+need+",实际拥有金币"+gold;
                }
                coinRemove(coinType.GOLD,need,userInfo,userInfoType);
                if (provider != null) {
                    UserCoinAdd.coinAdd(coinType.GOLD,need*3/10,provider,"userID");
                }
                break;
            case MIXED:
                if (need > gold+silver) {
                    return "货币不足！需要金币或银币"+need+",实际拥有金币"+gold+"及银币"+silver;
                }
                if (need <= silver) {
                    coinRemove(coinType.SILVER,need,userInfo,userInfoType);
                    if (provider != null) {
                        UserCoinAdd.coinAdd(coinType.SILVER,need*3/10,provider,"userID");
                    }
                } else {
                    coinRemove(coinType.SILVER,silver,userInfo,userInfoType);
                    int goldNeed = need-silver;
                    coinRemove(coinType.GOLD,goldNeed,userInfo,userInfoType);
                    if (provider != null) {
                        UserCoinAdd.coinAdd(coinType.SILVER,silver*3/10,provider,"userID");
                    }
                    if (provider != null) {
                        UserCoinAdd.coinAdd(coinType.GOLD,goldNeed*3/10,provider,"userID");
                    }
                }
                break;
        }
        return "pass";
    }

    public static void coinRemove(coinType coinType, Integer amount, String userInfo, String userInfoType) {
        if (userInfoType.equals("uid")) {
            userInfoType = "userID";
        }
        if (!userInfoType.equals("email") && !userInfoType.equals("userID") && !userInfoType.equals("qq")) {
            return;
            //return "交易失败，未知的用户信息参数！";
        }
        int coinHave = UserCoinGet.coinHave(userInfoType,userInfo,coinType);
        if (coinHave == -1) {
            return;
            //return "交易失败，用户数据查询失败！";
        }
        int coinNew = coinHave-amount;
        UserInfo.updateInfo(userInfoType,userInfo,coinType.name().toLowerCase(),String.valueOf(coinNew));
        sendInfo( "成功给 " + userInfo + " [" + userInfoType + "] " + "取走了 " + amount + " " + getCoinTypeName(coinType) + "!");
        sendInfo( "当前数量：" + coinNew);
        //return "交易成功，当前"+getCoinTypeName(coinType)+"数量为"+coinNew;
    }
}
