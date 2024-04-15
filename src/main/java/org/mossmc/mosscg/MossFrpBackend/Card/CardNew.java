package org.mossmc.mosscg.MossFrpBackend.Card;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;

import java.sql.SQLException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CardNew {
    public static String targetType;
    public static String targetAmount;

    public static int threadCount = 10;
    public static int completeThread = 0;

    public static int targetCount = 0;
    public static int succeedCount = 0;
    public static int perThreadCount = 0;

    public static long startTime;
    public static long stopTime;
    public static void createCard(int count,String type,String amount) {
        if (!type.equals("silver") && !type.equals("gold")) {
            sendWarn("未知的类型！");
            return;
        }
        sendInfo("正在创建中......");
        //记录时间
        startTime = System.currentTimeMillis();
        //参数
        targetType = type;
        targetAmount = amount;
        //线程初始化
        completeThread = 0;
        //统计数量初始化
        targetCount = count;
        succeedCount = 0;
        //运行线程
        perThreadCount = targetCount/threadCount;
        for (int i = 0;i<threadCount;i++) new Thread(() -> threadVoid(perThreadCount)).start();
    }

    public static void threadVoid(int count) {
        for (int i = 0;i<count;i++) {
            createVoid();
        }
        createComplete();
    }

    public static void createVoid() {
        try {
            String card = BasicInfo.getRandomString(16);
            JSONObject data = new JSONObject();
            data.put("code",card);
            data.put("type",targetType);
            data.put("amount",targetAmount);
            MysqlInsert.insert("card",data);
            succeedCount++;
        } catch (SQLException e) {
            sendException(e);
        }
    }

    public static synchronized void createComplete() {
        completeThread++;
        if (completeThread != threadCount) {
            return;
        }
        int remain = targetCount - perThreadCount*threadCount;
        for (int i=0;i<remain;i++) createVoid();
        stopTime = System.currentTimeMillis();
        long useTime = stopTime - startTime;
        sendInfo("创建完成！用时："+useTime+"毫秒，目标创建"+targetCount+"条，实际创建"+succeedCount+"条！");
    }
}
