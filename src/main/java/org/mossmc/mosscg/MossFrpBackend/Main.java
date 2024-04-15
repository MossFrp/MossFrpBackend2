package org.mossmc.mosscg.MossFrpBackend;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotConfirm;
import org.mossmc.mosscg.MossFrpBackend.Bot.BotCoolDown;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.File.FileCheck;
import org.mossmc.mosscg.MossFrpBackend.File.FileConfig;
import org.mossmc.mosscg.MossFrpBackend.File.FileDependency;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoGroup;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerWriter;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlConnection;
import org.mossmc.mosscg.MossFrpBackend.Node.*;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeCheck;
import org.mossmc.mosscg.MossFrpBackend.User.UserCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserIP;
import org.mossmc.mosscg.MossFrpBackend.Web.*;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;

public class Main {
    public static void main(String[] args) {
        //预加载阶段
        //包含Logger模块加载与基础信息加载
        //初始化依赖包以及文件生成
        BasicInfo.getTimeStart = System.currentTimeMillis();
        BasicInfo.checkThursday();
        BasicInfo.initBasicInfo(args);
        FileDependency.initDependency();
        FileCheck.check();
        FileConfig.checkConfig();
        FileConfig.loadConfigYaml();
        LoggerWriter.loadLogger();
        FileCheck.check();

        //显示一些信息
        InfoGroup.copyright();
        InfoGroup.logo();
        InfoGroup.runInfo();

        //启动引导
        StartGuide.runGuide();
    }

    public static void refreshCache() {
        //清除缓存
        try {
            LoggerSender.sendInfo("开始刷新缓存......");
            LoggerSender.sendInfo("正在清除IP黑名单");
            WebBlacklist.blacklistIPMap.clear();
            WebBlacklist.badRequestCountMap.clear();
            WebBlacklist.requestCountMap.clear();
            WebWhitelist.saveWhiteList();
            WebWhitelist.reloadWhiteList();
            LoggerSender.sendInfo("正在清除Client Token");
            TokenCache.tokenTimeMap.clear();
            TokenCache.tokenIPMap.clear();
            TokenCache.IPTokenMap.clear();
            TokenCache.tokenTypeMap.clear();
            LoggerSender.sendInfo("正在刷新用户缓存");
            UserCache.loadCache();
            UserIP.updateIP();
            LoggerSender.sendInfo("正在刷新节点信息缓存");
            NodeStatic.updateStatic();
            NodeCache.refreshCache();
            LoggerSender.sendInfo("正在刷新穿透码缓存");
            TimeCheck.runCheck();
            NodePort.loadEmptyPort();
            NodeNumber.loadUsedNumber();
            LoggerSender.sendInfo("正在刷新机器人缓存");
            UserPermission.permissionMap.clear();
            BotCoolDown.coolDownMap.clear();
            BotConfirm.confirmMap.clear();
            LoggerSender.sendInfo("正在更新Mysql链接");
            MysqlConnection.updatePoolConnection();
            System.gc();
            LoggerSender.sendInfo("正在刷新API服务缓存");
            WebClient.stop();
            WebNode.stop();
            WebAdvertisement.updateAll();
            WebShop.reloadShopData();
            WebDownload.reloadDownloadData();
            WebClient.load();
            WebNode.load();
            LoggerSender.sendInfo("缓存刷新完成！");
        } catch (Exception e) {
            LoggerSender.sendException(e);
            LoggerSender.sendWarn("缓存刷新失败！");
        }
    }

    public static void stop() {
        LoggerSender.sendInfo("正在关闭MossFrp后端！");
        LoggerSender.sendInfo("正在Frp进程！");
        NodeProcess.processIDMap.forEach((ID, process) -> process.destroy());
        LoggerSender.sendInfo("正在清理限速数据！");
        NodeBandLimit.initLimit();
        LoggerSender.sendInfo("已关闭MossFrp后端！");
        System.exit(0);
    }
}
