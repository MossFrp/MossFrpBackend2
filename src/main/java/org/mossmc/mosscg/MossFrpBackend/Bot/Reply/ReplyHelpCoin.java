package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyHelpCoin {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        send.append("【MossFrp Bot帮助】 \r\n");
        switch (permission) {
            case 999:
            case 900:
                send.append("你没有权限！ \r\n");
                break;
            case 600:
            case 300:
            case 100:
                send.append("购买卡密：#充值 \r\n");
                send.append("使用卡密：#卡密 \r\n");
                send.append("金币排行：#金币排行 \r\n");
                send.append("银币排行：#银币排行 \r\n");
                break;
            case 0:
                send.append("购买卡密：#充值 \r\n");
                send.append("使用卡密：#卡密 \r\n");
                send.append("更新金币：#金币 \r\n");
                send.append("更新银币：#银币 \r\n");
                send.append("金币排行：#金币排行 \r\n");
                send.append("银币排行：#银币排行 \r\n");
                break;
            default:
                break;
        }
        send.append("---==MossFrp==---");
    }
}
