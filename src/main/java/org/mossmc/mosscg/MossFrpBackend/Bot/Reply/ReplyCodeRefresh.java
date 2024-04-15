package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCodeRefresh {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 2) {
            send.append("指令帮助：#刷新穿透码 <节点> <编号>");
            return;
        }
        try {
            Integer.parseInt(command[2]);
            if (!NodeCache.nodeCache.containsKey(command[1])) {
                send.append("未知的节点！");
                return;
            }
        } catch (Exception e) {
            send.append("请填写正确的七位数字编号！如：1234567");
            return;
        }
        BotConfirm.inputConfirmMap(botType.name(),number,"task","code-refresh");
        BotConfirm.inputConfirmMap(botType.name(),number,"node",command[1]);
        BotConfirm.inputConfirmMap(botType.name(),number,"number",command[2]);
        send.append("你的刷新信息：\r\n节点：").append(command[1]).append("节点\r\n编号：").append(command[2]);
        send.append("\r\n此操作需要200货币！\r\n请输入 #确认 来确认你的操作，若输入有误，请重新发送指令！");
    }
}
