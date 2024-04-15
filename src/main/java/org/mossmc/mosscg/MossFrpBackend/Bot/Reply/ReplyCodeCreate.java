package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.util.Objects;

public class ReplyCodeCreate {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 3) {
            send.append("指令帮助：#新建穿透码 <节点> <带宽> <天数>");
            return;
        }
        if (!checkFormat(command)) {
            send.append("格式错误！示例：#新建穿透码 zz2 1 3");
            return;
        }
        String check = CodeInfoCheck.checkCreate(command[1],command[2],command[3],userID,permission);
        if (!check.equals("pass")) {
            send.append(check);
            return;
        }
        int band = Integer.parseInt(command[2]);
        int date = Integer.parseInt(command[3]);
        int price = Integer.parseInt(NodeCache.nodeCache.get(command[1]).get("price"));
        int needCoin = price * band * date;
        Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(command[1]).get("coin").toUpperCase());
        BotConfirm.inputConfirmMap(botType.name(),number,"task","code-new");
        BotConfirm.inputConfirmMap(botType.name(),number,"node",command[1]);
        BotConfirm.inputConfirmMap(botType.name(),number,"band",command[2]);
        BotConfirm.inputConfirmMap(botType.name(),number,"date",command[3]);
        send.append("你的穿透码信息：");
        send.append("\r\n节点：").append(command[1]).append("节点");
        send.append("\r\n带宽：").append(command[2]).append("Mbps");
        send.append("\r\n天数：").append(command[3]).append("天");
        send.append("\r\n费用：").append(needCoin).append(BasicInfo.getCoinTypeName(coinType));
        send.append("\r\n请输入 #确认 来确认你的操作，若输入有误，请重新发送指令！");
        send.append("\r\n备注：").append(NodeCache.nodeCache.get(command[1]).get("info"));
        if (NodeCache.nodeCache.get(command[1]).get("activity").equals("true")) {
            send.append("\r\nTips：此节点为特价节点，特价期间创建的穿透码将不可删除，请谨慎创建！");
        }
    }
    public static String notAllowedStrings = "<|>|节点|mbps|：|m|天|mb";

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
            if (message[2].toLowerCase().contains(cut[i])) {
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
