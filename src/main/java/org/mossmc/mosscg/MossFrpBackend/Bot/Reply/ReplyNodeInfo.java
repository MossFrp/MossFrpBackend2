package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyNodeInfo {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 300) {
            send.append("你没有权限！");
            return;
        }
        String nodeMessage;
        if (command.length > 1) {
            if (command.length > 2 && command[1].equals("all")) {
                if (command[2].equals("password") && permission > 0) {
                    send.append("你没有权限！");
                    return;
                }
                if (command.length > 3) {
                    nodeMessage = NodeCache.getNodeData(command[2],command[3]);
                } else {
                    nodeMessage = NodeCache.getNodeData(command[2],null);
                }
            } else {
                nodeMessage = NodeCache.getNodeStatus(command[1], "full");
            }
        } else {
            nodeMessage = NodeCache.getNodeStatus("all", "basic");
        }
        send.append(nodeMessage);
    }
}
