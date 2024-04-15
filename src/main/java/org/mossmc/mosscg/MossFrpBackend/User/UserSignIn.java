package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeString;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class UserSignIn {
    public static Map<Long,Integer> signIn;
    public static FileWriter getWriter;
    public static String fileDateSignIn;
    public static int coinMax;
    public static int coinMin;

    public static void writerUpdate() {
        String date = null;
        try {
            date = TimeString.getNowTimeString(Enums.timeStringType.DATE);
            if (getWriter != null) {
                getWriter.close();
            }
            String targetFilePath = "./MossFrp/SignIn/"+date+".yml";
            getWriter = new FileWriter(targetFilePath,true);
        } catch (IOException e) {
            sendException(e);
            sendWarn("签到模块写入功能初始化失败！");
        } finally {
            fileDateSignIn = date;
        }
    }

    public static void writerInput(Long uid,int userCoin) {
        try {
            getWriter.write("\r\n"+uid+": "+userCoin);
            getWriter.flush();
        } catch (IOException e) {
            sendException(e);
            sendWarn("签到缓存写入失败！");
        }
    }

    public static void loadUserSignIn() {
        signIn = new HashMap<>();
        coinMax = Integer.parseInt(getConfig("coinMax"));
        coinMin = Integer.parseInt(getConfig("coinMin"));
        String targetFilePath = "./MossFrp/SignIn";
        File targetFile = new File(targetFilePath);
        if (!targetFile.exists()) {
            if (!targetFile.mkdir()) {
                sendWarn("签到文件夹创建失败！");
            }
        }
        String date = TimeString.getNowTimeString(Enums.timeStringType.DATE);
        fileDateSignIn = date;
        targetFilePath = "./MossFrp/SignIn/"+date+".yml";
        targetFile = new File(targetFilePath);
        if (targetFile.exists()) {
            Yaml yaml = new Yaml();
            FileInputStream input = null;
            try {
                input = new FileInputStream(targetFile);
            } catch (Exception e) {
                sendException(e);
                sendWarn("签到缓存读取失败！");
            }
            Map<?,?> signInCache = yaml.loadAs(input, Map.class);
            if (signInCache != null) {
                sendInfo("正在读取签到缓存");
                Object user;
                long userID;
                int coinNumber;
                for (Object o : signInCache.keySet()) {
                    user = o;
                    userID = Long.parseLong(user.toString());
                    coinNumber = Integer.parseInt(signInCache.get(user).toString());
                    signIn.put(userID, coinNumber);
                }
                signInCache.clear();
            }
            sendInfo("签到缓存读取完成");
        }
        writerUpdate();
        sendInfo("签到模块已初始化完成");
    }

    public static Boolean getUserHaveGot(Long uid) {
        return signIn.containsKey(uid);
    }

    public synchronized static String getSignIn(Long uid) {
        String message;
        if (!TimeString.getNowTimeString(Enums.timeStringType.DATE).equals(fileDateSignIn)) {
            updateSignInDate();
        }
        if (!getUserHaveGot(uid)) {
            int coin = getCoin(uid);
            int luckCoin = UserLuck.getLuck(uid);
            int total = coin+luckCoin;
            UserCoinAdd.coinAdd(Enums.coinType.SILVER,total,String.valueOf(uid),"userID");
            signIn.put(uid,total);
            writerInput(uid,total);
            message = "今日签到成功！\r\n获得了"+total+"枚银币！";
        } else {
            int total = signIn.get(uid);
            message = "你今天已经签到过啦！\r\n获得了"+total+"枚银币~";
        }
        return message;
    }

    public static int getCoin(Long uid) {
        if (signIn.containsKey(uid)) {
            return signIn.get(uid);
        }
        return BasicInfo.getRandomInt(coinMax,coinMin);
    }

    public static void updateSignInDate() {
        sendInfo("日期已更新！签到缓存刷新中！");
        signIn.clear();
        sendInfo("已更新签到缓存");
        writerUpdate();
        sendInfo("已更新签到存储");
        sendInfo("签到模块日期更新完毕！");
    }
}
