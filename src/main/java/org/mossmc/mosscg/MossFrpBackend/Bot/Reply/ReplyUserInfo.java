package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyUserInfo {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 999) {
            send.append("你没有权限！");
            return;
        }
        String target = userID;
        if (permission <= 0 && command.length > 2) {
            target = UserInfo.getInfo(command[1],command[2],"userID");
        }
        if (target == null) {
            send.append("查询失败！");
            return;
        }
        JSONObject jsonObject = UserInfo.getUserInfo("userID",target);
        if (jsonObject == null) {
            send.append("查询失败！");
            return;
        }
        send.append("【用户信息】\r\n");
        send.append("编号：").append(jsonObject.getString("userID")).append("\r\n");
        send.append("名称：").append(jsonObject.getString("username")).append("\r\n");
        send.append("权限：").append(BasicInfo.getUserLevelName(jsonObject.getString("level"))).append("\r\n");
        send.append("金币：").append(jsonObject.getString("gold")).append("\r\n");
        send.append("银币：").append(jsonObject.getString("silver"));
        if (messageType.equals(BotMessage.messageType.Private)) {
            send.append("\r\n");
            send.append("邮箱：").append(jsonObject.getString("email"));
        }
    }
}
