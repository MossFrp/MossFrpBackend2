package org.mossmc.mosscg.MossFrpBackend.Bot.Reply;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;
import org.mossmc.mosscg.MossFrpBackend.Web.WebWhitelist;

public class ReplyAddIP {
    public static void getReply(BotMessage.botType botType, String[] command, String number, StringBuilder send, BotMessage.messageType messageType) {
        String userID = UserInfo.getInfo(botType.name(),number,"userID");
        int permission = UserPermission.getPermission(userID);
        if (permission > 100) {
            send.append("你没有权限！");
            return;
        }
        if (command.length <= 4) {
            send.append("指令帮助：#设置IP <加入/移出> <黑名单/白名单> <IP地址> <黑名单时长（秒）/白名单类型（FOREVER/TEMP）>");
            return;
        }
        if (command[2].equals("黑名单")) {
            try {
                if (command[1].equals("加入")) {
                    WebBlacklist.addBlacklistIP("QQBot手动添加", command[3], Integer.parseInt(command[4]));
                    send.append("操作成功！已添加黑名单IP：").append(command[3]).append(" 时长：").append(command[4]).append("秒");
                } else {
                    if (WebBlacklist.blacklistIPMap.containsKey(command[3])) {
                        WebBlacklist.blacklistIPMap.replace(command[3],WebBlacklist.blacklistIPMap.get(command[3])-Integer.parseInt(command[4])*1000L);
                        send.append("操作成功！已释放黑名单IP：").append(command[3]).append(" 释放时长：").append(command[4]).append("秒");
                    } else {
                        send.append("操作失败！黑名单不存在此IP！");
                    }
                }
            } catch (Exception e) {
                send.append("请输入正确的时长参数！");
            }
            return;
        }
        if (command[2].equals("白名单")) {
            Enums.whitelistType whitelistType;
            if (command[4].equalsIgnoreCase("FOREVER")) {
                whitelistType = Enums.whitelistType.FOREVER;
            } else {
                whitelistType = Enums.whitelistType.TEMP;
            }
            if (command[1].equals("加入")) {
                WebWhitelist.addWhiteList(command[3],whitelistType);
                send.append("操作成功！已添加白名单IP：").append(command[2]).append(" 类型：").append(whitelistType.name());
            } else {
                if (whitelistType.equals(Enums.whitelistType.FOREVER)) {
                    WebWhitelist.whitelistIPForever.remove(command[3]);
                } else {
                    WebWhitelist.whitelistIPTemp.remove(command[3]);
                }
                send.append("操作成功！已释放白名单IP：").append(command[3]).append(" 类型：").append(whitelistType.name());
            }
            return;
        }
        send.append("请确认您要添加的IP名单类型！");
    }
}
