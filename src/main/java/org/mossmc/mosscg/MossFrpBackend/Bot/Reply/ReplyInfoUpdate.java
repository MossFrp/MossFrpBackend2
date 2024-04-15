package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfoCheck;

public class ReplyInfoUpdate {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (!messageType.equals(BotMessage.messageType.Private)) {
            send.append("请在私聊使用此指令！");
            return;
        }
        if (command.length <= 2) {
            send.append("指令帮助：#更新信息 <email/name/password> <信息>");
            return;
        }
        if (command[1].equals("email")) {
            String emailCheck = UserInfoCheck.checkEmail(command[2]);
            if (!emailCheck.equals("pass")) {
                send.append(emailCheck);
                return;
            }
            emailCheck = UserInfoCheck.checkEmailRegister(command[2]);
            if (!emailCheck.equals("pass")) {
                send.append(emailCheck);
                return;
            }
            BotConfirm.inputConfirmMap(botType.name(),number,"task","update-user");
            BotConfirm.inputConfirmMap(botType.name(),number,"type","email");
            BotConfirm.inputConfirmMap(botType.name(),number,"info",command[2]);
            send.append("你的更新信息：\r\n类型：email\r\n信息：").append(command[2]);
            send.append("\r\n请输入 #确认 来确认你的操作，本操作将耗费200银币或金币，若需要修改，请使用 #更新信息 指令重新设置！");
            return;
            }
        if (command[1].equals("name")) {
            String nameCheck = UserInfoCheck.checkName(command[2]);
            if (!nameCheck.equals("pass")) {
                send.append(nameCheck);
                return;
            }
            BotConfirm.inputConfirmMap(botType.name(),number,"task","update-user");
            BotConfirm.inputConfirmMap(botType.name(),number,"type","name");
            BotConfirm.inputConfirmMap(botType.name(),number,"info",command[2]);
            send.append("你的更新信息：\r\n类型：name\r\n信息：").append(command[2]);
            send.append("\r\n请输入 #确认 来确认你的操作，本操作将耗费100银币或金币，若需要修改，请使用 #更新信息 指令重新设置！");
            return;
        }
        if (command[1].equals("password")) {
            String passwordCheck = UserInfoCheck.checkPassword(command[2]);
            if (!passwordCheck.equals("pass")) {
                send.append(passwordCheck);
                return;
            }
            BotConfirm.inputConfirmMap(botType.name(),number,"task","update-user");
            BotConfirm.inputConfirmMap(botType.name(),number,"type","password");
            BotConfirm.inputConfirmMap(botType.name(),number,"info",command[2]);
            send.append("你的更新信息：\r\n类型：password\r\n信息：").append(command[2]);
            send.append("\r\n请输入 #确认 来确认你的操作，本操作将耗费50银币或金币，若需要修改，请使用 #更新信息 指令重新设置！");
            return;
        }
        send.append("指令帮助：#更新信息 <email/name/password> <信息>");
    }
}
