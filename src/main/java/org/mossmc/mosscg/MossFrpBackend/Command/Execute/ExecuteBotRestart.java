package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Bot.BotQQ;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMoss3;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserPassword;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteBotRestart {
    public static void execute() {
        sendInfo("正在重启MossFrp-QQBot");
        BotQQ.startBot();
        sendInfo("MossFrp-QQBot重启完成");
    }
}
