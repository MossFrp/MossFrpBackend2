package org.mossmc.mosscg.MossFrpBackend.User;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeBan;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class UserBan {
    public static String banUser(String key,String value,String reason) {
        StringBuilder message = new StringBuilder();
        try {
            JSONObject userData = UserInfo.getUserInfo(key, value);
            if (userData == null) {
                message.append("未知的用户信息！请检查！");
            } else {
                String userID = userData.getString("userID");
                String info = userData.getString("info")+"Banned at "+ TimeString.getNowTimeString(Enums.timeStringType.FULL) +" Reason: "+reason+"/";
                UserInfo.updateInfo("userID",userID,"level","banned");
                UserInfo.updateInfo("userID",userID,"info",info);
                UserPermission.permissionMap.remove(userID);
                TokenCache.removeUserToken(userID);
                JSONObject userCode = CodeInfo.getUserCodeInfo(userID);
                assert userCode != null;
                userCode.forEach((count, jsonString) -> {
                    JSONObject json = (JSONObject) jsonString;
                    String codeBan = CodeBan.banCode(json.getString("node"),json.getString("number"),"User was banned!");
                    sendInfo(codeBan);
                });
                MailSend.sendBannedUser(userData.getString("email"),reason);
                message.append("封禁成功！此用户及其全部穿透码已被封禁！");
            }
        } catch (Exception e) {
            sendException(e);
            message.append("数据查询出错！请检查！");
        }
        return message.toString();
    }

    public static String unbanUser(String key,String value,String reason) {
        StringBuilder message = new StringBuilder();
        try {
            JSONObject userData = UserInfo.getUserInfo(key, value);
            if (userData == null) {
                message.append("未知的用户信息！请检查！");
            } else {
                String userID = userData.getString("userID");
                String info = userData.getString("info")+"UnBanned at "+ TimeString.getNowTimeString(Enums.timeStringType.FULL) +" Reason: "+reason+"/";
                UserInfo.updateInfo("userID",userID,"level","default");
                UserInfo.updateInfo("userID",userID,"info",info);
                UserPermission.permissionMap.remove(userID);
                TokenCache.removeUserToken(userID);
                message.append("解封成功！此用户已解封！其名下还有以下穿透码被封禁：");
                JSONObject userCode = CodeInfo.getUserCodeInfo(userID);
                assert userCode != null;
                userCode.forEach((count, jsonString) -> {
                    JSONObject json = (JSONObject) jsonString;
                    message.append("\r\n").append(json.getString("node")).append(" | ").append(json.getString("number"));
                });
                MailSend.sendUnBannedUser(userData.getString("email"),reason);
            }
        } catch (Exception e) {
            sendException(e);
            message.append("数据查询出错！请检查！");
        }
        return message.toString();
    }
}
