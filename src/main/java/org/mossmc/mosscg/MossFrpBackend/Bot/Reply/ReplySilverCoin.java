package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinAdd;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplySilverCoin {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 0) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 3) {
            send.append("指令帮助：#银币 <qq/email/userID> <信息> <数量>");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(command[3]);
        } catch (Exception e) {
            send.append("请输入正确的参数！");
            return;
        }
        String check = UserCoinAdd.coinAdd(Enums.coinType.SILVER,amount,command[2],command[1]);
        send.append(check);
    }
}
