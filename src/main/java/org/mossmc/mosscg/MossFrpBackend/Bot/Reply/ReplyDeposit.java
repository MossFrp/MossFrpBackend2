package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyDeposit {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        send.append("请使用网页购买卡密\r\n");
        send.append("[卡密&无偿赞助|支持微信支付宝] 爱发电：https://afdian.net/a/MossFrp\r\n");
        send.append("[卡密|支持QQ支付宝] MCRMB：http://www.mcrmb.com/fk/24184\r\n");
        send.append("购买卡密后使用指令 #卡密 来激活卡密！");
    }
}
