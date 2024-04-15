package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class UserCoinRank {
    public static Map<Integer,String> goldRank = new HashMap<>();
    public static Map<Integer,String> silverRank = new HashMap<>();

    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread rankUpdate = new Thread(UserCoinRank::autoUpdate);
        rankUpdate.setName("coinRankUpdateThread");
        singleThreadExecutor.execute(rankUpdate::start);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void autoUpdate() {
        while (true) {
            updateRank();
        }
    }

    public static void updateRank() {
        try {
            ResultSet set = MysqlGetResult.getResultSet("SELECT * FROM user ORDER BY `silver` DESC LIMIT 10");
            int rank = 0;
            while (set.next()) {
                rank++;
                silverRank.put(rank,"|-"+rank+"."+set.getString("username")+" —— "+set.getString("silver"));
            }
            set = MysqlGetResult.getResultSet("SELECT * FROM user ORDER BY `gold` DESC LIMIT 10");
            rank = 0;
            while (set.next()) {
                rank++;
                goldRank.put(rank,"|-"+rank+"."+set.getString("username")+" —— "+set.getString("gold"));
            }
        } catch (Exception e) {
            sendException(e);
            sendWarn("更新用户货币排行出现错误！");
        } finally {
            try {
                Thread.sleep(1000L*60);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static String getCoinRank(Enums.coinType coinType) {
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if (coinType.equals(Enums.coinType.GOLD)) {
            stringBuilder.append("MossFrp金币排行榜");
            while (i < 10) {
                i++;
                stringBuilder.append("\r\n").append(goldRank.get(i));
            }
        }
        if (coinType.equals(Enums.coinType.SILVER)) {
            stringBuilder.append("MossFrp银币排行榜");
            while (i < 10) {
                i++;
                stringBuilder.append("\r\n").append(silverRank.get(i));
            }
        }
        return stringBuilder.toString();
    }
}
