package org.mossmc.mosscg.MossFrpBackend;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotMessage;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.Command.CommandCenter;
import org.mossmc.mosscg.MossFrpBackend.Command.CommandNode;
import org.mossmc.mosscg.MossFrpBackend.File.FileClear;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoLoad;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoNotice;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoSystem;
import org.mossmc.mosscg.MossFrpBackend.Mail.MailMain;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlConnection;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlThread;
import org.mossmc.mosscg.MossFrpBackend.Node.*;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeCheck;
import org.mossmc.mosscg.MossFrpBackend.User.UserLuck;
import org.mossmc.mosscg.MossFrpBackend.User.UserMain;
import org.mossmc.mosscg.MossFrpBackend.User.UserSignIn;
import org.mossmc.mosscg.MossFrpBackend.Web.*;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class StartGuide {
    public static void runGuide() {
        sendInfo("正在引导启动，启动模式："+BasicInfo.getRunType.name());
        switch (BasicInfo.getRunType) {
            case NODE:
                guideNode();
                break;
            case CENTER:
                guideCenter();
                break;
            default:
                sendError("未知的启动模式！请检查启动参数！");
                System.exit(1);
        }
        long runTime = System.currentTimeMillis() - BasicInfo.getTimeStart;
        double usedTime = (double) runTime/1000;
        sendInfo("启动完成！耗时："+usedTime+"秒！");
        BasicInfo.start = true;
    }

    public static void guideNode() {
        //启动数据统计线程
        InfoLoad.sendLoadStart("SystemData");
        InfoSystem.runThread();
        InfoLoad.sendLoadComplete("SystemData");

        //初始化API信息
        InfoLoad.sendLoadStart("CenterAPI");
        NodeAPIConnection.initAPIInfo();
        InfoLoad.sendLoadComplete("CenterAPI");

        //启动指令模块
        InfoLoad.sendLoadStart("Command");
        CommandNode.commandThread();
        InfoLoad.sendLoadComplete("Command");

        //初始化Process模块
        InfoLoad.sendLoadStart("FrpProcess");
        NodeProcess.clearProcess();
        FileClear.clear();
        NodeBandLimit.initLimit();
        NodeProcess.runThread();
        InfoLoad.sendLoadComplete("FrpProcess");

        //启动心跳包线程
        InfoLoad.sendLoadStart("Heartbeat");
        NodeHeartbeat.runThread();
        InfoLoad.sendLoadComplete("Heartbeat");
    }

    public static void guideCenter() {
        //提前加载机器人模块
        //登录专用，平时注释掉
        //InfoLoad.sendLoadStart("BotQQ");
        //BotMessage.registerAll();
        //BotQQ.startBot();
        //InfoLoad.sendLoadComplete("BotQQ");

        //启动Mysql链接
        InfoLoad.sendLoadStart("Mysql");
        MysqlConnection.updatePoolConnection();
        MysqlThread.runThread();
        InfoLoad.sendLoadComplete("Mysql");

        //启动数据统计线程
        InfoLoad.sendLoadStart("SystemData");
        InfoSystem.updateInfo();
        InfoSystem.runThread();
        InfoNotice.runThread();
        InfoLoad.sendLoadComplete("SystemData");

        //启动邮箱模块
        InfoLoad.sendLoadStart("Mail");
        MailMain.loadMail();
        InfoLoad.sendLoadComplete("Mail");

        //启动节点模块
        InfoLoad.sendLoadStart("Node");
        NodeMain.initNodeModule();
        InfoLoad.sendLoadComplete("Node");

        //启动用户模块
        InfoLoad.sendLoadStart("User");
        UserMain.initUserModule();
        UserLuck.inputUserLuck();
        UserSignIn.loadUserSignIn();
        InfoLoad.sendLoadComplete("User");

        //加载WebAPI
        InfoLoad.sendLoadStart("WebAPI");
        WebBasic.checkWebFile();
        WebAdvertisement.runThread();
        WebShop.updateThread();
        WebDownload.updateThread();
        WebWhitelist.reloadWhiteList();
        WebWhitelist.saveThread();
        WebBlacklist.checkThread();
        WebNode.load();
        WebClient.load();
        InfoLoad.sendLoadComplete("WebAPI");

        //加载时间检查模块
        InfoLoad.sendLoadStart("TimeCheck");
        TimeCheck.checkDateThread();
        InfoLoad.sendLoadComplete("TimeCheck");

        //加载机器人模块
        if (getConfig("botEnabled").equals("true")) {
            InfoLoad.sendLoadStart("BotQQ");
            BotMessage.registerAll();
            BotQQ.startBot();
            InfoLoad.sendLoadComplete("BotQQ");
        }

        //启动指令模块
        InfoLoad.sendLoadStart("Command");
        CommandCenter.commandThread();
        InfoLoad.sendLoadComplete("Command");
    }
}
