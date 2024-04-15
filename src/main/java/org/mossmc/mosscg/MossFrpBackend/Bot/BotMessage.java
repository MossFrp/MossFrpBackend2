package org.mossmc.mosscg.MossFrpBackend.Bot;

import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class BotMessage {
    public enum messageType {
        Group,Private
    }
    public enum botType {
        qq
    }
    public static Object object;
    public static Map<String,Method> commandMap = new HashMap<>();
    public static StringBuilder getReply(botType bot,String message,String number,messageType type) {
        StringBuilder send = new StringBuilder();
        String[] args = message.split("\\s+");
        String userID = UserInfo.getInfo(bot.name(),number,"userID");
        try {
            if (userID == null && type == messageType.Group) {
                switch (args[0]) {
                    case "#绑定邮箱":
                        commandMap.get("#绑定邮箱").invoke(object,bot,args,number,send,type);
                        break;
                    case "#确认":
                        commandMap.get("#确认").invoke(object,bot,args,number,send,type);
                        break;
                    case "#确定":
                        commandMap.get("#确定").invoke(object,bot,args,number,send,type);
                        break;
                    default:
                        commandMap.get("#注册").invoke(object,bot,args,number,send,type);
                        break;
                }
            } else {
                if (commandMap.containsKey(args[0])) {
                    commandMap.get(args[0]).invoke(object,bot,args,number,send,type);
                } else {
                    commandMap.get("#未知指令").invoke(object,bot,args,number,send,type);
                }
            }

        } catch (Exception e) {
            sendException(e);
            return null;
        }
        if (message.contains("<") || message.contains(">")) {
            send.append("\r\n热知识Tips：指令中，'< >'代表必填参数，'[ ]'代表选填参数，实际使用的时候不需要带括号哦~");
        }
        return send;
    }

    public static void registerAll() {
        try {
            Class<?> mainClass = Class.forName("org.mossmc.mosscg.MossFrpBackend.Main");
            object = mainClass.newInstance();
        } catch (Exception e) {
            sendException(e);
        }
        registerCommand("#增加天数","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyAddAllDay");
        registerCommand("#设置IP","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyAddIP");
        registerCommand("#广告","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyAdvertisement");
        registerCommand("#绑定邮箱","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyBindEmail");
        registerCommand("#卡密","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCard");
        registerCommand("#封禁穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeBan");
        registerCommand("#升配穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeBand");
        registerCommand("#新建穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeCreate");
        registerCommand("#续期穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeDate");
        registerCommand("#穿透码列表","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeList");
        registerCommand("#穿透码邮件","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeMail");
        registerCommand("#刷新穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeRefresh");
        registerCommand("#删除穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeRemove");
        registerCommand("#解封穿透码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeUnBan");
        registerCommand("#新建激活码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#删除激活码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#激活码列表","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#激活码邮件","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#升配激活码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#续期激活码","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCommandOutdated");
        registerCommand("#确认","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyConfirm");
        registerCommand("#确定","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyConfirm");
        registerCommand("#充值","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyDeposit");
        registerCommand("#金币","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyGoldCoin");
        registerCommand("#金币排行","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyGoldCoinRank");
        registerCommand("#帮助","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyHelp");
        registerCommand("#穿透码帮助","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyHelpCode");
        registerCommand("#货币帮助","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyHelpCoin");
        registerCommand("#更新信息","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyInfoUpdate");
        registerCommand("#今日人品","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyLuck");
        registerCommand("#节点信息","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyNodeInfo");
        registerCommand("#节点列表","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyNodeList");
        registerCommand("#刷新缓存","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyRefreshCache");
        registerCommand("#注册","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyRegister");
        registerCommand("#签到","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplySignIn");
        registerCommand("#银币","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplySilverCoin");
        registerCommand("#银币排行","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplySilverCoinRank");
        registerCommand("#统计信息","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyStatic");
        registerCommand("#测试","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyTest");
        registerCommand("#未知指令","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyUnknownCommand");
        registerCommand("#封禁用户","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyUserBan");
        registerCommand("#用户信息","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyUserInfo");
        registerCommand("#解封用户","org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyUserUnBan");
        sendInfo("机器人指令注册完成");
    }

    public static void registerCommand(String command,String className) {
        try {
            Class<?> methodClass;
            methodClass = Class.forName(className);
            commandMap.put(command, methodClass.getDeclaredMethod("getReply", botType.class, String[].class, String.class, StringBuilder.class, messageType.class));
            sendInfo("已注册机器人指令："+command);
        } catch (Exception e) {
            sendException(e);
            sendWarn("无法注册机器人指令："+command);
        }
    }
}
