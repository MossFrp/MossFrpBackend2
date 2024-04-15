package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserLuck;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;

public class ReplySignIn {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        assert userID != null;
        long uid = Long.parseLong(userID);
        String nowDate = TimeString.getTimeString(Enums.timeStringType.DATE,System.currentTimeMillis());
        assert nowDate != null;
        if (!UserLuck.getUserHaveGot(uid)|| !nowDate.equals(UserLuck.fileDateLuck)) {
            int luck = UserLuck.getLuck(uid);
            String luckMessage = UserLuck.getLuckMessage(luck);
            send.append("你今日的人品值为：").append(luck).append(luckMessage).append("\r\n");
        }
        String signInMessage = UserSignIn.getSignIn(uid);
        send.append(signInMessage);
    }
}
