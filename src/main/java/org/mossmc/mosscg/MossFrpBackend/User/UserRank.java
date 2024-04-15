package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class UserRank {
    public static Map<Integer,String> goldRank = new HashMap<>();
    public static Map<Integer,String> silverRank = new HashMap<>();

    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread rankUpdate = new Thread(UserRank::autoUpdate);
        rankUpdate.setName("userRankUpdateThread");
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
                silverRank.put(rank,set.getString("username")+"|"+set.getString("silver"));
            }

            set = MysqlGetResult.getResultSet("SELECT * FROM user ORDER BY `gold` DESC LIMIT 10");
            rank = 0;

            while (set.next()) {
                rank++;
                goldRank.put(rank,set.getString("username")+"|"+set.getString("gold"));
            }
        } catch (Exception e) {
            sendException(e);
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
        switch (coinType) {
            case GOLD:
                stringBuilder.append("MossFrp金币排行榜");
                while (i < 10) {
                    i++;
                    String[] cut = goldRank.get(i).split("\\|");
                    stringBuilder.append("\r\n|-").append(cut[0]);
                    stringBuilder.append(" --- ").append(cut[1]);
                }
                break;
            case SILVER:
                stringBuilder.append("MossFrp银币排行榜");
                while (i < 10) {
                    i++;
                    String[] cut = silverRank.get(i).split("\\|");
                    stringBuilder.append("\r\n|-").append(cut[0]);
                    stringBuilder.append(" --- ").append(cut[1]);
                }
                break;
            default:
                return null;
        }
        return stringBuilder.toString();
    }
}
