package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;
import org.mossmc.mosscg.MossFrpBackend.Web.WebWhitelist;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class ExecuteAddIP {
    public static void execute(String type,String IP,String dataString,String control) {
        switch (control) {
            case "add":
                switch (type) {
                    case "whitelist":
                        Enums.whitelistType whitelistType;
                        if (dataString.equalsIgnoreCase("FOREVER")) {
                            whitelistType = Enums.whitelistType.FOREVER;
                        } else {
                            whitelistType = Enums.whitelistType.TEMP;
                        }
                        WebWhitelist.addWhiteList(IP,whitelistType);
                        sendInfo("已添加"+IP+"为白名单，类型："+dataString);
                        break;
                    case "blacklist":
                        WebBlacklist.addBlacklistIP("手动添加",IP,Integer.parseInt(dataString));
                        sendInfo("已添加"+IP+"为黑名单");
                        break;
                    default:
                        sendWarn("未知黑白名单内容！");
                        break;
                }
                break;
            case "remove":
                switch (type) {
                    case "whitelist":
                        WebWhitelist.whitelistIPTemp.remove(IP);
                        WebWhitelist.whitelistIPForever.remove(IP);
                        sendInfo("已移除"+IP+"的白名单！");
                        break;
                    case "blacklist":
                        WebBlacklist.blacklistIPMap.remove(IP);
                        sendInfo("已移除"+IP+"的黑名单");
                        break;
                    default:
                        sendWarn("未知黑白名单内容！");
                        break;
                }
                break;
            default:
                sendWarn("未知操作！");
                break;
        }
    }
}
