package org.mossmc.mosscg.MossFrpBackend.Bot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.contact.announcement.Announcement;
import net.mamoe.mirai.contact.announcement.AnnouncementParameters;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class BotQQ {
    public static Bot bot = null;
    public static String commandPrefix;
    public static List<Long> groupList;
    public static List<Long> adminList;
    public static void startBot() {
        try {
            sendInfo("正在初始化机器人模块");
            if (bot != null && bot.isOnline()) {
                bot.close();
            }
            File dataFile = new File(getConfig("botFile"));
            String qq = getConfig("botNumber");
            String password = getConfig("botPassword");
            String groups = getConfig("allowGroups");
            String admins = getConfig("allowAdmins");
            groupList = new ArrayList<>();
            adminList = new ArrayList<>();
            commandPrefix = getConfig("botCommandPrefix");
            String[] cut = groups.split(",");
            for (String s : cut) {
                groupList.add(Long.valueOf(s));
                sendInfo("开启消息群更新：" + s);
            }
            cut = admins.split(",");
            for (String s : cut) {
                adminList.add(Long.valueOf(s));
                sendInfo("管理用户更新：" + s);
            }
            if (!dataFile.exists()) {
                sendInfo("机器人运行文件目录不存在！正在创建目录");
                if (dataFile.mkdir()) {
                    sendInfo("成功创建机器人运行文件目录");
                } else {
                    sendWarn("机器人运行文件目录创建失败！已取消机器人启动！");
                    return;
                }
            }
            sendInfo("机器人模块初始化完成");
            sendInfo("机器人账号："+qq);
            BotConfiguration botConfiguration = new BotConfiguration() {{
                if (getConfig("botLog").equals("false")) {
                    noBotLog();
                    sendInfo("已关闭机器人信息日志显示");
                }if (getConfig("botNetworkLog").equals("false")) {
                    noNetworkLog();
                    sendInfo("已关闭机器人网络日志显示");
                }
                switch (getConfig("botHeartbeat")) {
                    case "1":
                        setHeartbeatStrategy(HeartbeatStrategy.STAT_HB);
                        sendInfo("机器人心跳策略：STAT_HB");
                        break;
                    case "2":
                        setHeartbeatStrategy(HeartbeatStrategy.REGISTER);
                        sendInfo("机器人心跳策略：REGISTER");
                        break;
                    case "3":
                        setHeartbeatStrategy(HeartbeatStrategy.NONE);
                        sendInfo("心跳策略参数设置错误！使用默认机器人心跳策略：NONE");
                        break;
                    default:
                        setHeartbeatStrategy(HeartbeatStrategy.NONE);
                        sendInfo("机器人心跳策略：NONE");
                        break;
                }
                switch (getConfig("botProtocol")) {
                    case "1":
                        setProtocol(MiraiProtocol.ANDROID_PHONE);
                        sendInfo("机器人登陆协议：ANDROID_PHONE");
                        break;
                    case "2":
                        setProtocol(MiraiProtocol.ANDROID_PAD);
                        sendInfo("机器人登陆协议：ANDROID_PAD");
                        break;
                    case "3":
                        setProtocol(MiraiProtocol.ANDROID_WATCH);
                        sendInfo("机器人登陆协议：ANDROID_WATCH");
                        break;
                    case "4":
                        setProtocol(MiraiProtocol.IPAD);
                        sendInfo("机器人登陆协议：IPAD");
                        break;
                    case "5":
                        setProtocol(MiraiProtocol.MACOS);
                        sendInfo("机器人登陆协议：MACOS");
                        break;
                    default:
                        setProtocol(MiraiProtocol.ANDROID_PHONE);
                        sendInfo("协议参数设置错误！使用默认机器人登陆协议：ANDROID_PHONE");
                        break;
                }
                sendInfo("机器人工作目录："+dataFile);
                setWorkingDir(dataFile);
                fileBasedDeviceInfo();
            }};
            if (getConfig("botLoginType").equals("1")) {
                bot = BotFactory.INSTANCE.newBot(Long.parseLong(qq), password, botConfiguration);
            } else {
                bot = BotFactory.INSTANCE.newBot(Long.parseLong(qq), BotAuthorization.byQRCode(), botConfiguration);
            }
            sendInfo("正在登陆机器人账号");
            bot.login();
            sendInfo("正在启动机器人消息监听线程");
            botListenerThread();
        } catch (Exception e) {
            sendException(e);
            sendWarn("QQ机器人启动失败！请检查报错！");
        }
    }

    public static void botListenerThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread botListener = new Thread(BotQQ::listenerThread);
        botListener.setName("botListenerThread");
        singleThreadExecutor.execute(botListener);
        sendInfo( "机器人消息监听线程已启动！");
    }

    public static void listenerThread () {
        Listener<?> listener;
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            MessageChain chain = event.getMessage();
            String output = "[BotReceive][Group]"+"["+ event.getGroup().getId()+"]"+ event.getSender().getId()+": "+chain.contentToString();
            sendInfo(output);
            readMessage(event.getGroup(), event.getSender(), chain.contentToString(), BotMessage.messageType.Group);
        });
        listener.start();
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, event -> {
            MessageChain chain = event.getMessage();
            String output = "[BotReceive][GroupTemp]"+"["+ event.getGroup().getId()+"]"+"["+ event.getSender().getId()+"]"+": "+chain.contentToString();
            sendInfo(output);
            readMessage(event.getGroup(), event.getSender(), chain.contentToString(), BotMessage.messageType.Private);
        });
        listener.start();
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            MessageChain chain = event.getMessage();
            String output = "[BotReceive][Friend]"+"["+ event.getSender().getId()+"]"+": "+chain.contentToString();
            sendInfo(output);
            readMessage(null, event.getSender(), chain.contentToString(), BotMessage.messageType.Private);
        });
        listener.start();
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(StrangerMessageEvent.class, event -> {
            MessageChain chain = event.getMessage();
            String output = "[BotReceive][Stranger]"+"["+ event.getSender().getId()+"]"+": "+chain.contentToString();
            sendInfo(output);
            readMessage(null, event.getSender(), chain.contentToString(), BotMessage.messageType.Private);
        });
        listener.start();
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, event -> {
            if (event.getGroup() == null) {
                return;
            }
            if (!groupList.contains(Objects.requireNonNull(event.getGroup()).getId())) {
                return;
            }
            event.accept();
            String output = "[BotJoin][GroupRequest]"+"["+ event.getGroup().getId()+"]["+ event.getFromId()+"]"+": "+event.getMessage();
            sendInfo(output);
        });
        listener.start();
        sendInfo( "机器人监听线程已启动");
    }

    public static void readMessage(Group group, User user, String message, BotMessage.messageType type) {
        if (!message.startsWith(commandPrefix) || !allowReply(group,user,type)) {
            return;
        }
        StringBuilder stringBuilder;
        stringBuilder = BotMessage.getReply(BotMessage.botType.qq,message,String.valueOf(user.getId()),type);
        if (stringBuilder == null) {
            return;
        }
        String output = "";
        switch (type) {
            case Group:
                String at = "\r\n@"+user.getNick();
                stringBuilder.append(at);
                group.sendMessage(stringBuilder.toString());
                output = "[BotSend]["+type.name()+"]"+"["+ group.getId()+"]"+ user.getId()+": "+stringBuilder;
                break;
            case Private:
                user.sendMessage(stringBuilder.toString());
                if (group != null) {
                    output = "[BotSend]["+type.name()+"]"+"["+ group.getId()+"]"+ user.getId()+": "+stringBuilder;
                } else {
                    output = "[BotSend]["+type.name()+"]"+ user.getId()+": "+stringBuilder;
                }
                break;
            default:
                output = "Unknown Type!";
        }
        sendInfo(output);
    }

    public static boolean allowReply(Group group, User user, BotMessage.messageType type) {
        //用户判断
        if (user == null) {
            return false;
        }
        if (BotQQ.adminList.contains(user.getId())) {
            return true;
        }
        //群判断
        if (group != null && BotQQ.groupList.contains(group.getId())) {
            return true;
        }
        //是否在群内判断
        if (type == BotMessage.messageType.Private) {
            int i = 0;
            while (i < groupList.size()) {
                Group groupGet = bot.getGroup(groupList.get(i));
                if (groupGet == null) {
                    continue;
                }
                Member memberGet = groupGet.get(user.getId());
                if (memberGet != null) {
                    return true;
                }
                i = i + 1;
            }
        }
        return false;
    }

    public static void sendAdminMessage(String message) {
        try {
            if (bot == null || !bot.isOnline()) {
                return;
            }
            for (long number : adminList) {
                Friend admin = bot.getFriend(number);
                if (admin != null) {
                    admin.sendMessage(message);
                }
                String output = "[BotSend][Admin]"+ number+": "+message;
                sendInfo(output);
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void sendGroupMessage(String number, String message) {
        try {
            if (bot == null || !bot.isOnline()) {
                return;
            }
            Group group;
            try {
                group = bot.getGroup(Long.parseLong(number));
            } catch (NumberFormatException e) {
                sendWarn("不合法的群号：" + number);
                return;
            }
            if (group != null) {
                group.sendMessage(message);
                sendInfo("已发送群消息：[" + number + "]" + message);
            } else {
                sendWarn("群消息发送失败，参数：[" + number + "]" + message);
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void sendUserMessage(String number, String message) {
        try {
            if (bot == null || !bot.isOnline()) {
                return;
            }
            User user;
            try {
                user = bot.getFriend(Long.parseLong(number));
                if (user == null) {
                    user = bot.getStranger(Long.parseLong(number));
                }
            } catch (NumberFormatException e) {
                sendWarn("不合法的QQ号：" + number);
                return;
            }
            if (user != null) {
                user.sendMessage(message);
                sendInfo("已发送私聊消息：[" + number + "]" + message);
            } else {
                sendWarn("私聊消息发送失败，参数：[" + number + "]" + message);
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void sendGroupMemberMessage(String groupNumber, String memberNumber, String message) {
        try {
            if (bot == null || !bot.isOnline()) {
                return;
            }
            Group group;
            Member member;
            try {
                group = bot.getGroup(Long.parseLong(groupNumber));
                if (group != null) {
                    member = group.get(Long.parseLong(memberNumber));
                    if (member == null) {
                        sendWarn("群" + groupNumber + "内找不到用户" + memberNumber);
                        return;
                    }
                } else {
                    sendWarn("未知的群号：" + groupNumber);
                    return;
                }
            } catch (NumberFormatException e) {
                sendWarn("不合法的群号或QQ号：" + groupNumber);
                return;
            }
            member.sendMessage(message);
            sendInfo("已发送群私聊消息：[" + groupNumber + "][" + memberNumber + "]" + message);
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void sendGroupAnnouncement(String content, long groupID) {
        try {
            if (bot == null || !bot.isOnline()) {
                return;
            }
            Group group = bot.getGroup(groupID);
            assert group != null;
            Announcement announcement = Announcement.publishAnnouncement(group,content);
            group.getAnnouncements().publish(announcement);
            sendInfo("已发送群公告：[" + groupID + "]" + content);
        } catch (Exception e) {
            sendException(e);
        }
    }
}
