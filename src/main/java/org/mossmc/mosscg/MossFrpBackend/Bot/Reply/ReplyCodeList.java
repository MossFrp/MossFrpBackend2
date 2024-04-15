package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCodeList {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        String infoMode;
        if (messageType.equals(BotMessage.messageType.Group)) {
            infoMode = "light";
        } else {
            infoMode = "full";
        }
        if (command.length > 2 && permission <= 100) {
            String requestUser = UserInfo.getInfo(command[1],command[2],"userID");
            if (requestUser == null) {
                send.append("无法查询到该用户！");
                return;
            }
            String messageSend = CodeInfo.getUserAllAsString(Long.parseLong(requestUser),infoMode);
            send.append(messageSend);
            return;
        }
        assert userID != null;
        String messageSend = CodeInfo.getUserAllAsString(Long.parseLong(userID),infoMode);
        send.append(messageSend);
    }
}
