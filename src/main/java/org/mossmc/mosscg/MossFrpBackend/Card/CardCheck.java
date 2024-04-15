package org.mossmc.mosscg.MossFrpBackend.Card;

import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CardCheck {
    public static void check() {
        try {
            sendInfo("正在检查中");
            Long startTime = System.currentTimeMillis();
            List<String> cards = new ArrayList<>();
            ResultSet set = MysqlGetResult.getResultSet("select * from card");
            while (set.next()) {
                String card = set.getString("code");
                if (cards.contains(card)) {
                    sendWarn("出现重复的卡密！参数："+set.getString("ID")+" | "+card);
                } else {
                    cards.add(card);
                }
            }
            cards.clear();
            System.gc();
            Long endTime = System.currentTimeMillis();
            long timeUse = endTime - startTime;
            sendInfo("检查完成");
            sendInfo("共耗时" + timeUse + "毫秒");
        } catch (Exception e) {
            sendException(e);
        }
    }
}
