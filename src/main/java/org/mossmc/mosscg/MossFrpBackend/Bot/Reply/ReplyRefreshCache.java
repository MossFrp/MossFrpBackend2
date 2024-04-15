package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Main;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyRefreshCache {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 100) {
            send.append("你没有权限！");
            return;
        }
        long memoryUsage = Runtime.getRuntime().freeMemory();
        Main.refreshCache();
        long newMemoryUsage = Runtime.getRuntime().freeMemory();
        double free = (double) (newMemoryUsage - memoryUsage);
        free = free / 1024;
        free = free / 1024;
        send.append("已刷新MossFrp后端缓存，回收内存").append(String.format("%.2f", free)).append("MB");
    }
}
