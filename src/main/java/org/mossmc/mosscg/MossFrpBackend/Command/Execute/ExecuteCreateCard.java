package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Card.CardNew;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteCreateCard {
    public static void execute(String count,String type,String amount) {
        try {
            CardNew.createCard(Integer.parseInt(count),type,amount);
        } catch (Exception e) {
            sendException(e);
        }
        sendInfo("已完成操作！");
    }
}
