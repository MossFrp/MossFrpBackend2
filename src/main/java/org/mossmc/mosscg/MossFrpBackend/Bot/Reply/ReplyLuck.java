package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserLuck;

public class ReplyLuck {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        assert userID != null;
        long uid = Long.parseLong(userID);
        int luck = UserLuck.getLuck(uid);
        String luckMessage = UserLuck.getLuckMessage(luck);
        send.append("你今日的人品值为：").append(luck).append(luckMessage);
    }
}
