package org.mossmc.mosscg.MossFrpBackend.User;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlUpdate;

import java.sql.ResultSet;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class UserInfo {
    public static JSONObject getUserInfo(String key, String value) {
        JSONObject info = new JSONObject();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from user where "+key+"=?",value);
            if (set.next()) {
                info.put("userID",set.getString("userID"));
                info.put("password",set.getString("password"));
                info.put("username",set.getString("username"));
                info.put("email",set.getString("email"));
                info.put("qq",set.getString("qq"));
                info.put("level",set.getString("level"));
                info.put("gold",set.getString("gold"));
                info.put("silver",set.getString("silver"));
                info.put("info",set.getString("info"));
            } else {
                return null;
            }
            return info;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static JSONObject getBasicUserInfo(String key, String value) {
        JSONObject info = new JSONObject();
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from user where "+key+"=?",value);
            if (set.next()) {
                info.put("userID",set.getString("userID"));
                info.put("username",set.getString("username"));
                info.put("email",set.getString("email"));
                info.put("qq",set.getString("qq"));
                info.put("level",set.getString("level"));
                info.put("gold",set.getString("gold"));
                info.put("silver",set.getString("silver"));
                info.put("signIn",UserSignIn.getUserHaveGot(set.getLong("userID")));
            } else {
                return null;
            }
            return info;
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static String getInfo(String key,String value,String target) {
        JSONObject userData = getUserInfo(key, value);
        if (userData == null) {
            return null;
        } else {
            return userData.getString(target);
        }
    }

    public static void updateInfo(String updateKey,String updateValue,String key,String value) {
        try {
            String sql = "update user set "+key+"='"+value+ "' WHERE "+updateKey+"='"+updateValue+"'";
            MysqlUpdate.execute(sql);
        } catch (Exception e) {
            sendException(e);
        }
    }
}
