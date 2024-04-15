package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getRandomString;

public class ReplyRegister {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission == 999) {
            //if (true) {
            //    send.append("请在 https://panel.mossfrp.top/#/register 进行注册，QQBot注册渠道已关闭！若您已注册，请使用 #绑定邮箱 指令获取相关帮助！");
            //    return;
            //}
            if (command[0].equals("#注册") && messageType == BotMessage.messageType.Group) {
                send.append("请通过【私聊】发送 #注册 来获取注册帮助！若您已在网页注册，请使用 #绑定邮箱 指令获取相关帮助！");
            } else {
                if (messageType == BotMessage.messageType.Group) {
                    send.append("您还未注册！请先【私聊】机器人使用 #注册 来注册账号！若您已在网页注册，请使用 #绑定邮箱 指令获取相关帮助！");
                } else {
                    if (command.length >= 4) {
                        if (!checkFormat(command)) {
                            send.append("格式错误！示例：#注册 123456@qq.com HaoYe 12345678");
                            return;
                        }
                        BotConfirm.inputConfirmMap(botType.name(),number,"task","register");
                        BotConfirm.inputConfirmMap(botType.name(),number, botType.name(), number);
                        BotConfirm.inputConfirmMap(botType.name(),number, "type", botType.name());
                        BotConfirm.inputConfirmMap(botType.name(),number, "email", command[1]);
                        BotConfirm.inputConfirmMap(botType.name(),number, "name", command[2]);
                        BotConfirm.inputConfirmMap(botType.name(),number, "password", command[3]);
                        send.append("你的注册信息：\r\n邮箱：").append(command[1])
                                .append("\r\n名称：").append(command[2])
                                .append("\r\n密码：").append(command[3]);
                        send.append("\r\n请输入#确认 来确认你的操作，若输入有误，请重新使用注册指令注册！");
                        return;
                    }
                    if (command.length == 1) {
                        if (botType == BotMessage.botType.qq) {
                            String password = getRandomString(10);
                            String name = "User_" + number;
                            String email = number + "@qq.com";
                            BotConfirm.inputConfirmMap(botType.name(),number, "task", "register");
                            BotConfirm.inputConfirmMap(botType.name(),number, botType.name(), number);
                            BotConfirm.inputConfirmMap(botType.name(),number, "type", botType.name());
                            BotConfirm.inputConfirmMap(botType.name(),number, "email", email);
                            BotConfirm.inputConfirmMap(botType.name(),number, "name", name);
                            BotConfirm.inputConfirmMap(botType.name(),number, "password", password);
                            send.append("你的快速注册信息：\r\n邮箱：").append(email).append("\r\n名称：").append(name).append("\r\n密码：").append(password);
                            send.append("\r\n请输入 #确认 来确认你的操作，若需要自定义信息，请使用 #注册 <邮箱> <名称> <密码> 来注册！");
                            return;
                        }
                    }
                    send.append("指令帮助：#注册 <邮箱> <名称> <密码>");
                }
            }
        } else {
            send.append("您已经注册了！");
        }
    }

    public static String notAllowedStrings = "<|>|邮箱|名称|密码|：";

    public static boolean checkFormat(String[] message) {
        if (message.length != 4) {
            return false;
        }
        String[] cut = notAllowedStrings.split("\\|");
        int i = 0;
        while (i < cut.length) {
            if (message[1].contains(cut[i])) {
                return false;
            }
            if (message[2].contains(cut[i])) {
                return false;
            }
            if (message[3].contains(cut[i])) {
                return false;
            }
            i++;
        }
        return true;
    }
}
