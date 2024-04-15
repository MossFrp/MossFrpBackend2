package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyBindEmail {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission != 999) {
            send.append("您已经注册了！");
            return;
        }
        if (command.length <= 1) {
            send.append("指令帮助：#绑定邮箱 <email>");
            return;
        }
        BotConfirm.inputConfirmMap(botType.name(),number, "task", "bind-email");
        BotConfirm.inputConfirmMap(botType.name(),number, botType.name(), number);
        BotConfirm.inputConfirmMap(botType.name(),number, "type", botType.name());
        BotConfirm.inputConfirmMap(botType.name(),number, "email", command[1]);
        send.append("你的绑定信息：\r\n邮箱：").append(command[1]);
        send.append("\r\nTips：此指令仅用于网站注册用户绑定QQ");
        send.append("\r\n请输入#确认 来确认你的操作，若输入有误，请重新使用 #绑定邮箱 指令设置！");
    }
}
