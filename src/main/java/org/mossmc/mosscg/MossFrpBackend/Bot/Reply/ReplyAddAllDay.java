package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeBan;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeDate;
import org.mossmc.mosscg.MossFrpBackend.User.UserIP;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;

import java.util.List;

public class ReplyAddAllDay {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 0) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 2) {
            send.append("指令帮助：#增加天数 <节点/all> <天数>");
            return;
        }
        List<String> result;
        if (command[1].equals("all")) {
            result = CodeDate.addAllDay(Integer.parseInt(command[2]));
        } else {
            result = CodeDate.addNodeAllDay(command[1],Integer.parseInt(command[2]));
        }
        if (result == null) {
            send.append("操作失败！结果为空值！");
        } else {
            send.append("操作成功！已为以下穿透码增加了").append(command[2]).append("天的时长！\r\n");
            send.append(UserIP.listToString(result));
        }
    }
}
