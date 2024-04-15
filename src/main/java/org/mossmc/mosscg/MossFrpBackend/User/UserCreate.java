package org.mossmc.mosscg.MossFrpBackend.User;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;

import java.sql.SQLException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class UserCreate {
    public static synchronized void createUser(String username, String email, String password, JSONObject extraInfo, String regFrom) {
        JSONObject userData;
        if (extraInfo == null) {
            userData = new JSONObject();
        } else {
            userData = extraInfo;
        }
        userData.put("username",username);
        userData.put("email",email.toLowerCase());
        userData.put("password", EncryptMain.encode(password));
        userData.put("silver",3500);
        try {
            MysqlInsert.insert("user",userData);
            UserCache.nameCache.add(username);
            UserCache.emailCache.add(email);
            if (userData.containsKey("qq")){
                UserCache.qqCache.add(userData.getString("qq"));
            }
        } catch (SQLException e) {
            LoggerSender.sendException(e);
        }
        String message = "MossFrp有新用户注册！\r\n邮箱：" + email + "\r\n名称：" + username + "\r\n渠道：" + regFrom;
        sendInfo(message);
        BotQQ.sendAdminMessage(message);
    }
}
