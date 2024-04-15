package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Card.CardCheck;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteCheckCard {
    public static void execute() {
        try {
            CardCheck.check();
        } catch (Exception e) {
            sendException(e);
        }
        sendInfo("已完成操作！");
    }
}
