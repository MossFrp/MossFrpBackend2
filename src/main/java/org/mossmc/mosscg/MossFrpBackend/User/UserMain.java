package org.mossmc.mosscg.MossFrpBackend.User;

public class UserMain {
    public static void initUserModule() {
        UserPassword.inputEasyPassword();
        UserLuck.inputUserLuck();
        UserRank.updateThread();
        UserCache.loadCache();
        UserCache.runThread();
        UserCoinRank.updateThread();
        UserIP.updateThread();
    }
}
