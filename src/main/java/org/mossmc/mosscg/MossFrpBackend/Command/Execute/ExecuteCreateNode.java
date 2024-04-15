package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Node.NodeCreate;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteCreateNode {
    public static void execute(String nodeCode,String nodeAddress,String port) {
        NodeCreate.create(nodeCode, nodeAddress, port);
        sendInfo("已完成操作！");
    }
}
