package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotCoolDown;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Card.CardUse;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCard {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 1) {
            send.append("指令帮助：#卡密 <卡密>");
            return;
        }
        if (BotCoolDown.coolDown(botType.name(), number, "card")) {
            send.append("指令冷却中~请稍后~");
            return;
        }
        String check = CardUse.cardUse(command[1],botType.name(),number);
        if (check.contains("[failed]")) {
            send.append(check.replace("[failed]",""));
            BotCoolDown.setCoolDown(botType.name(),number,10,"card");
        } else {
            send.append(check.replace("[success]",""));
        }
    }
}
