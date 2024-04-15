package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Web.WebBasic;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendWarn;

public class ExecuteDisplayAPI {
    public static void execute(String type,String result) {
        if (!type.equals("node") && !type.equals("client")) {
            sendWarn("参数错误！可用参数：node client");
            return;
        }
        if (type.equalsIgnoreCase("node")) {
            WebBasic.displayNodeAPI = Boolean.parseBoolean(result);
        }
        if (type.equalsIgnoreCase("client")) {
            WebBasic.displayClientAPI = Boolean.parseBoolean(result);
        }
    }
}
