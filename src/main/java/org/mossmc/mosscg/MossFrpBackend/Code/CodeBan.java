package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CodeBan {
    public static String getCodeBan(String node,String number) {
        JSONObject data = CodeInfo.getCodeInfoByNodeNumber(node,number);
        if (data == null) {
            return "unknown";
        }
        if (data.getString("status").equals("banned")) {
            return "true";
        } else {
            return "false";
        }
    }

    public static String banCode(String node,String number,String reason) {
        JSONObject data = CodeInfo.getCodeInfoByNodeNumber(node,number);
        if (data == null) {
            return "unknown";
        }
        if (data.getString("status").equals("banned")) {
            return "banned";
        }
        sendInfo( "正在封禁穿透码，参数："+node+" | "+number+" 原因："+reason);
        CodeInfo.updateCodeInfo("ID", data.getString("ID"),"status","banned");
        NodeCache.nodeUpdate.add(node);
        JSONObject userData = UserInfo.getUserInfo("userID",data.getString("user"));
        assert userData != null;
        MailSend.sendBannedCode(userData.getString("email"),node,number,reason);
        return "success";
    }

    public static String unbanCode(String node,String number,String reason) {
        String banned = getCodeBan(node, number);
        if (banned.equals("false") || banned.equals("unknown")) {
            return "unbanned";
        }
        sendInfo( "正在解封穿透码，参数："+node+" | "+number);
        JSONObject data = CodeInfo.getCodeInfoByNodeNumber(node, number);
        if (data == null) {
            return "unknown";
        }
        long deadline = Long.parseLong(data.getString("stop"));
        if (deadline <= System.currentTimeMillis()) {
            CodeInfo.updateCodeInfo("ID", data.getString("ID"),"status","outdated");
        } else {
            CodeInfo.updateCodeInfo("ID", data.getString("ID"),"status","run");
        }
        NodeCache.nodeUpdate.add(node);
        JSONObject userData = UserInfo.getUserInfo("userID",data.getString("user"));
        assert userData != null;
        MailSend.sendUnBannedCode(userData.getString("email"),node,number,reason);
        return "success";
    }
}
