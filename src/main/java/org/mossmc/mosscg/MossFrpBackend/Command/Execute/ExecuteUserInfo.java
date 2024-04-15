package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteUserInfo {
    public static void execute(String key,String value) {
        JSONObject info = UserInfo.getUserInfo(key, value);
        if (info == null) {
            sendInfo("无法查询到数据！");
        } else {
            sendInfo(info.toString());
        }
    }
}
