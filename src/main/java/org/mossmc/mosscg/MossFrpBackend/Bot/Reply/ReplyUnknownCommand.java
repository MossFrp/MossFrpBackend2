package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyUnknownCommand {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission == 999) {
            send.append("请使用 #注册 指令获取注册帮助！");
            return;
        }
        if (permission >= 900) {
            send.append("你没有权限！");
            return;
        }
        send.append("未知指令！请使用 #帮助 查看指令列表！");
    }
}
