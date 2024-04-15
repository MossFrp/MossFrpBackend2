package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyHelpCode {
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
                send.append("创建穿透码：#新建穿透码 \r\n");
                send.append("更新穿透码：#刷新穿透码 \r\n");
                send.append("续费穿透码：#续期穿透码 \r\n");
                send.append("升级穿透码：#升配穿透码 \r\n");
                send.append("移除穿透码：#删除穿透码 \r\n");
                send.append("穿透码列表：#穿透码列表 \r\n");
                send.append("邮件穿透码：#穿透码邮件 \r\n");
                break;
            case 0:
                send.append("创建穿透码：#新建穿透码 \r\n");
                send.append("更新穿透码：#刷新穿透码 \r\n");
                send.append("续费穿透码：#续期穿透码 \r\n");
                send.append("升级穿透码：#升配穿透码 \r\n");
                send.append("移除穿透码：#删除穿透码 \r\n");
                send.append("穿透码列表：#穿透码列表 \r\n");
                send.append("邮件穿透码：#穿透码邮件 \r\n");
                send.append("封禁穿透码：#封禁穿透码 \r\n");
                send.append("解封穿透码：#解封穿透码 \r\n");
                break;
            default:
                break;
        }
        send.append("---==MossFrp==---");
    }
}
