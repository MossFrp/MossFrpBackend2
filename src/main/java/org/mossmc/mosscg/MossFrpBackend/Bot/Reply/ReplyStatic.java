package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;

public class ReplyStatic {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 300) {
            send.append("你没有权限！");
            return;
        }
        send.append("MossFrp统计数据");
        send.append("\r\n用户数量：").append(UserCache.userCount);
        send.append("\r\n在线隧道：").append(UserCache.codeCount);
        send.append("\r\n可用节点：").append(UserCache.nodeCount);
        send.append("\r\n签到用户：").append(UserSignIn.signIn.size());
        send.append("\r\n主控内存：").append(InfoSystem.getMemoryUsed).append("/").append(InfoSystem.getMemoryTotal);
        send.append("\r\n主控负载：").append(InfoSystem.getCPUUsage);
        send.append("\r\n主控带宽：↑").append(InfoSystem.getUploadBand).append("/↓").append(InfoSystem.getDownloadBand);
        send.append("\r\n主控流量：↑").append(InfoSystem.getUploadTotal).append("/↓").append(InfoSystem.getDownloadTotal);
    }
}
