package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class ExecuteUpdateUser {
    public static void execute(String userID,String key,String value) {
        JSONObject data = UserInfo.getUserInfo("userID",userID);
        if (data == null) {
            sendWarn("未知的用户！");
            return;
        }
        if (!data.containsKey(key)) {
            sendWarn("未知的信息Key！");
            return;
        }
        if (key.equals("password")) {
            value = EncryptMain.encode(value);
        }
        UserInfo.updateInfo("userID",userID,key,value);
        sendInfo("已将用户"+userID+"的"+key+"信息由"+data.getString(key)+"更改为"+value);
    }
}
