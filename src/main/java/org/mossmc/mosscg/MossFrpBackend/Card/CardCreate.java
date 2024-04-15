package org.mossmc.mosscg.MossFrpBackend.Card;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;

import java.sql.SQLException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CardCreate {
    public static String targetType;
    public static String targetAmount;
    public static int targetCount;
    public static int succeedCount;
    public static int threadCount = 10;
    public static int completeThread;

    public static long startTime;

    public static void createCard(int count,String type,String amount) {
        if (!type.equals("silver") && !type.equals("gold")) {
            sendWarn("未知的类型！");
            return;
        }
        sendInfo("正在创建中......");
        startTime = System.currentTimeMillis();
        targetType = type;
        targetAmount = amount;
        targetCount = count;
        succeedCount = 0;
        for (int i = 0;i<targetCount;i++) {
            createVoid(type,amount);
        }
        completeThread=10;
        createComplete();
    }

    public static void createCardAsync(int count,String type,String amount) {
        if (!type.equals("silver") && !type.equals("gold")) {
            sendWarn("未知的类型！");

            return;
        }
        sendInfo("正在创建中......");

        startTime = System.currentTimeMillis();
        targetType = type;
        targetAmount = amount;
        targetCount = count;
        succeedCount = 0;
        completeThread = 0;

        for (int i = 0;i<threadCount;i++) {
            new Thread(() -> {
                createThread(count/threadCount,type,amount);
                completeThread++;
                createComplete();
            }).start();
        }

    }

    public static void createThread(int count,String type,String amount) {
        while (count > 0) {
            count--;
            createVoid(type,amount);
        }
    }

    public static void createVoid(String type,String amount) {
        try {
            String card = BasicInfo.getRandomString(16);
            JSONObject data = new JSONObject();
            data.put("code",card);
            data.put("type",type);
            data.put("amount",amount);
            MysqlInsert.insert("card",data);
            succeedCount++;
        } catch (SQLException e) {
            sendException(e);
        }
    }

    public static void createComplete() {
        if (completeThread != threadCount) {
            return;
        }
        int remain = targetCount-succeedCount;
        createThread(remain,targetType,targetAmount);
        long stopTime = System.currentTimeMillis();
        long useTime = stopTime - startTime;
        sendInfo("创建完成！用时："+useTime+"毫秒，目标创建"+targetCount+"条，实际创建"+succeedCount+"条！");
    }
}
