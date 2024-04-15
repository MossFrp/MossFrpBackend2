package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeBan;
import org.mossmc.mosscg.MossFrpBackend.User.UserBan;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;

public class ReplyUserBan {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 0) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 3) {
            send.append("指令帮助：#封禁用户 <key> <value> <原因>");
            return;
        }
        StringBuilder reason = new StringBuilder();
        for (int i =3;i< command.length;i++) {
            reason.append(command[i]).append(" ");
        }
        String check = UserBan.banUser(command[1],command[2],reason.toString());
        send.append(check);
    }
}
