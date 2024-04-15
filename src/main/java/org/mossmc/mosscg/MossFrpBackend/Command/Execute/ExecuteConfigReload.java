package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.File.FileConfig;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteConfigReload {
    public static void execute() {
        sendInfo("正在重载配置文件");
        FileConfig.loadConfigYaml();
        sendInfo("重载配置文件完成");
    }
}
