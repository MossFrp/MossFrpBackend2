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

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class UserLuck {
    public static Map<Long,Integer> luck;
    public static FileWriter getWriter;
    public static String fileDateLuck;
    public static void writerUpdate() {
        String date = null;
        try {
            date = TimeString.getNowTimeString(Enums.timeStringType.DATE);
            if (getWriter != null) {
                getWriter.close();
            }
            String targetFilePath = "./MossFrp/Luck/"+date+".yml";
            getWriter = new FileWriter(targetFilePath,true);
        } catch (IOException e) {
            sendException(e);
            sendWarn("今日人品模块写入功能初始化失败！");
        } finally {
            fileDateLuck = date;
        }
    }

    public static void writerInput(Long uid,int userLuck) {
        try {
            getWriter.write("\r\n"+uid+": "+userLuck);
            getWriter.flush();
        } catch (IOException e) {
            sendException(e);
            sendWarn("用户人品缓存写入失败！");
        }
    }

    public static void inputUserLuck() {
        luck = new HashMap<>();
        String targetFilePath = "./MossFrp/Luck";
        File targetFile = new File(targetFilePath);
        if (!targetFile.exists()) {
            if (!targetFile.mkdir()) {
                sendWarn("用户今日人品文件夹创建失败！");
            }
        }
        String date = TimeString.getNowTimeString(Enums.timeStringType.DATE);
        fileDateLuck = date;
        targetFilePath = "./MossFrp/Luck/"+date+".yml";
        targetFile = new File(targetFilePath);
        if (targetFile.exists()) {
            Yaml yaml = new Yaml();
            FileInputStream input = null;
            try {
                input = new FileInputStream(targetFile);
            } catch (Exception e) {
                sendException(e);
                sendWarn("用户今日人品缓存读取失败！");
            }
            Map<?,?> luckCache = yaml.loadAs(input, Map.class);
            if (luckCache != null) {

                sendInfo("正在读取今日人品缓存");
                Object user;
                long userID;
                int luckNumber;
                for (Object o : luckCache.keySet()) {
                    user = o;
                    userID = Long.parseLong(user.toString());
                    luckNumber = Integer.parseInt(luckCache.get(user).toString());
                    luck.put(userID, luckNumber);
                }
                luckCache.clear();
            }
            sendInfo("用户今日人品缓存读取完成");
        }
        writerUpdate();
        sendInfo("用户今日人品模块已初始化完成");
    }

    public static Boolean getUserHaveGot(Long uid) {
        return luck.containsKey(uid);
    }

    public synchronized static int getLuck(Long uid) {
        if (!TimeString.getNowTimeString(Enums.timeStringType.DATE).equals(fileDateLuck)) {
            updateLuckDate();
        }
        if (luck.containsKey(uid)) {
            return luck.get(uid);
        }
        int userLuck = BasicInfo.getRandomInt(100,1);
        luck.put(uid,userLuck);
        writerInput(uid,userLuck);
        return userLuck;
    }

    public static void updateLuckDate() {
        sendInfo("日期已更新！今日人品缓存刷新中！");
        luck.clear();
        sendInfo("已更新今日人品缓存");
        writerUpdate();
        sendInfo("已更新今日人品存储");
        sendInfo("今日人品模块日期更新完毕！");
    }

    public static String getLuckMessage(int userLuck) {
        //愚人节彩蛋
        //if (true) return "，今天，又是猪咪的一天~";

        if (userLuck == 100) return "，呜呜呜呜呜呜呜呜呜呜呜欧皇贴贴！（扒拉）（试图吸走欧气）";
        if (userLuck >= 90) return "，诶多~就差一点点就满分了呢~";
        if (userLuck >= 80) return "，今天的运气看起来不错呢喵！适合到处转转哦！";
        if (userLuck >= 70) return "，诶诶诶？七十多分也很高的说！还可以再努力！";
        if (userLuck >= 60) return "，虽然说普普通通，但至少跨过了及格线哦！";
        if (userLuck >= 50) return "，杂鱼~杂鱼~还差一点就及格了哦❤~";
        if (userLuck >= 40) return "，四十多可不低啦！快要接近一百的一半啦！";
        if (userLuck == 39) return "，是39！三玖天下第一！（夹带私货.jpg）";
        if (userLuck >= 30) return "，抽到任何一个数字都是百分之一的概率哦！都是很厉害的事情！";
        if (userLuck >= 20) return "，太差劲了（二乃摊手.jpg），明天肯定会更高的！";
        if (userLuck >= 10) return "，嘛嘛，最起码是两位数！也是不错的运气！";
        if (userLuck >= 0) return "，喵？虽然说才个位数，但是也是十分之一的概率，也是一种幸运哦~！";
        return "，诶诶？好像数据不太对？";
    }
}
