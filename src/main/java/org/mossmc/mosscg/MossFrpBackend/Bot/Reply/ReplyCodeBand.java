package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeRemain;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyCodeBand {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(), number, "userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 600) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 3) {
            send.append("指令帮助：#升配穿透码 <节点> <编号> <带宽>");
            return;
        }
        String check = CodeInfoCheck.checkBand(command[1], command[2], botType.name(), number, command[3]);
        if (!check.equals("pass")) {
            send.append(check);
            return;
        }
        int band = Integer.parseInt(command[3]);
        int date = TimeRemain.dateRemainDay(command[1],command[2]);
        int price = Integer.parseInt(NodeCache.nodeCache.get(command[1]).get("price"));
        int needCoin = price * band * date;
        Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(command[1]).get("coin").toUpperCase());
        BotConfirm.inputConfirmMap(botType.name(), number, "task", "code-band");
        BotConfirm.inputConfirmMap(botType.name(), number, "node", command[1]);
        BotConfirm.inputConfirmMap(botType.name(), number, "number", command[2]);
        BotConfirm.inputConfirmMap(botType.name(), number, "band", command[3]);
        BotConfirm.inputConfirmMap(botType.name(), number, "coin", String.valueOf(needCoin));
        send.append("你的升配信息：");
        send.append("\r\n节点：").append(command[1]).append("节点");
        send.append("\r\n编号：").append(command[2]);
        send.append("\r\n带宽：+").append(command[3]).append("Mbps");
        send.append("\r\n费用：").append(needCoin).append(BasicInfo.getCoinTypeName(coinType));
        send.append("\r\n请输入 #确认 来确认你的操作，若输入有误，请重新发送指令！");
        if (NodeCache.nodeCache.get(command[1]).get("activity").equals("true")) {
            send.append("\r\nTips：此节点为特价节点，特价期间升配的穿透码将不可删除，请谨慎升配！");
        }
    }
}
