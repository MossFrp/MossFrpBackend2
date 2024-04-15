package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotCoolDown;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailSend;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCodeMail {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (BotCoolDown.coolDown(botType.name(),number,"CodeMail")) {
            send.append("冷却中，请稍后~");
            return;
        }
        BotCoolDown.setCoolDown(botType.name(),number,120,"CodeMail");
        String mail = UserInfo.getInfo("userID",userID,"email");
        if (command.length > 2 && permission <= 0) {
            String requestUser = UserInfo.getInfo(command[1],command[2],"userID");
            if (requestUser == null) {
                send.append("无法查询到该用户！");
                return;
            }
            String messageSend = CodeInfo.getUserAllAsString(Long.parseLong(requestUser),"all");
            MailSend.sendCodeAll(mail,messageSend);
            send.append("已发送穿透码列表邮件！请查收~如果没收到且邮箱的垃圾箱也没有那就是被判定垃圾邮件了，建议使用网页端操作哦~");
            return;
        }
        assert userID != null;
        String messageSend = CodeInfo.getUserAllAsString(Long.parseLong(userID),"all");
        MailSend.sendCodeAll(mail,messageSend);
        send.append("已发送穿透码列表邮件！请查收~如果没收到且邮箱的垃圾箱也没有那就是被判定垃圾邮件了，建议使用网页端操作哦~");
    }
}
