package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.User.UserIP;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteCheckIP {
    public static void execute(String max) {
        UserIP.checkSameIP(Integer.parseInt(max));
        sendInfo("已完成操作！");
    }
}
