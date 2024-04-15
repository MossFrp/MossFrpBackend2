package org.mossmc.mosscg.MossFrpBackend.Card;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlDelete;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinAdd;

import java.sql.ResultSet;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CardUse {
    public static String cardUse(String card, String infoType, String info) {
        try {
            ResultSet set = MysqlGetResult.getResultSet("select * from card where code=?",card);
            if (set.next()) {
                String amountString = set.getString("amount");
                int amount;
                if (amountString.contains("#rd#")) {
                    amount = CardRandom.getCardRandom(amountString);
                } else {
                    amount = Integer.parseInt(amountString);
                }
                String type = set.getString("type");
                Enums.coinType typeCoin;
                switch (type) {
                    case "silver":
                        typeCoin = Enums.coinType.SILVER;
                        break;
                    case "gold":
                        typeCoin = Enums.coinType.GOLD;
                        break;
                    default:
                        typeCoin = Enums.coinType.SILVER;
                        break;
                }

                MysqlDelete.execute("card", "ID", set.getString("ID"));
                UserCoinAdd.coinAdd(typeCoin, amount, info, infoType);
                sendCardUsage("已使用卡密：" + card + " | " + set.getString("ID") + " | " + set.getString("type") + " | " + set.getString("amount") + " | " + amount);
                return "[success]使用成功！你获得了" + amount + BasicInfo.getCoinTypeName(typeCoin)+"！";
            } else {
                return "[failed]未知的卡密！请确认卡密是否正确！";
            }
        } catch (Exception e) {
            sendException(e);
        }
        return "[failed]发生错误！查询失败！";
    }
}
