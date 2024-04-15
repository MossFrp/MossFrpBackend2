package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeBan;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCodeUnBan {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 0) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 3) {
            send.append("指令帮助：#解封穿透码 <节点> <编号> <原因>");
            return;
        }
        StringBuilder reason = new StringBuilder();
        for (int i =3;i< command.length;i++) {
            reason.append(command[i]).append(" ");
        }
        String check = CodeBan.unbanCode(command[1],command[2],reason.toString());
        String reply = "unknown";
        if (check.equals("success")) {
            reply = "解封成功！";
        }
        if (check.equals("failed")) {
            reply = "解封失败！";
        }
        if (check.equals("unbanned")) {
            reply = "此穿透码未被封禁！";
        }
        send.append(reply);
    }
}
