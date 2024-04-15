package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Code.CodeCreate;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteCreateCode {
    public static void execute(String nodeCode,String band,String day) {
        try {
            CodeCreate.create(nodeCode,"10000000",Integer.parseInt(band),Integer.parseInt(day));
        } catch (Exception e) {
            sendException(e);
        }
        sendInfo("已完成操作！");
    }
}
