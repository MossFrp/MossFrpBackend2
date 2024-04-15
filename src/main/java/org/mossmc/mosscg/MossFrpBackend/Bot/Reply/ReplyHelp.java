package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

public class ReplyHelp {
    public static void getReply(BotMessage.botType botType,String[] command,String number,StringBuilder send,BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        send.append("【MossFrp Bot帮助】 \r\n");
        switch (permission) {
            case 999:
                send.append("注册：#注册 \r\n");
                break;
            case 900:
                send.append("个人信息：#用户信息 \r\n");
                break;
            case 600:
                send.append("个人信息：#用户信息 \r\n");
                send.append("修改信息：#更新信息 \r\n");
                send.append("今日人品：#今日人品 \r\n");
                send.append("每日签到：#签到 \r\n");
                send.append("节点列表：#节点列表 \r\n");
                send.append("货币相关：#货币帮助 \r\n");
                send.append("穿透码相关：#穿透码帮助 \r\n");
                break;
            case 300:
                send.append("个人信息：#用户信息 \r\n");
                send.append("修改信息：#更新信息 \r\n");
                send.append("今日人品：#今日人品 \r\n");
                send.append("每日签到：#签到 \r\n");
                send.append("广告管理：#广告 \r\n");
                send.append("节点状态：#节点信息 \r\n");
                send.append("节点列表：#节点列表 \r\n");
                send.append("统计数据：#统计信息 \r\n");
                send.append("货币相关：#货币帮助 \r\n");
                send.append("穿透码相关：#穿透码帮助 \r\n");
                break;
            case 100:
                send.append("个人信息：#用户信息 \r\n");
                send.append("修改信息：#更新信息 \r\n");
                send.append("今日人品：#今日人品 \r\n");
                send.append("每日签到：#签到 \r\n");
                send.append("广告管理：#广告 \r\n");
                send.append("节点状态：#节点信息 \r\n");
                send.append("节点列表：#节点列表 \r\n");
                send.append("统计数据：#统计信息 \r\n");
                send.append("货币相关：#货币帮助 \r\n");
                send.append("穿透码相关：#穿透码帮助 \r\n");
                send.append("刷新缓存：#刷新缓存 \r\n");
                send.append("测试信息：#测试 \r\n");
                break;
            case 0:
                send.append("查询信息：#用户信息 \r\n");
                send.append("修改信息：#更新信息 \r\n");
                send.append("今日人品：#今日人品 \r\n");
                send.append("每日签到：#签到 \r\n");
                send.append("广告管理：#广告 \r\n");
                send.append("节点状态：#节点信息 \r\n");
                send.append("节点列表：#节点列表 \r\n");
                send.append("统计数据：#统计信息 \r\n");
                send.append("货币相关：#货币帮助 \r\n");
                send.append("穿透码相关：#穿透码帮助 \r\n");
                send.append("刷新缓存：#刷新缓存 \r\n");
                send.append("封禁用户：#封禁用户 \r\n");
                send.append("解封用户：#解封用户 \r\n");
                send.append("测试信息：#测试 \r\n");
                break;
            default:
                break;
        }
        send.append("---==MossFrp==---");
    }
}
