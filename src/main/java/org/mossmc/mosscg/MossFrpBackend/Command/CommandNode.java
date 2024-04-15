package org.mossmc.mosscg.MossFrpBackend.Command;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Command.Execute.ExecuteDisplayAPI;
import org.mossmc.mosscg.MossFrpBackend.Command.Execute.ExecuteStatus;
import org.mossmc.mosscg.MossFrpBackend.Command.Execute.ExecuteStop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CommandNode {
    public static void commandThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread commandListen = new Thread(CommandNode::commandInput);
        commandListen.setName("commandListenerThread");
        singleThreadExecutor.execute(commandListen);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void commandInput() {
        try {
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
            while (true){
                String command = bufferedReader.readLine();
                sendCommand(command);
                readCommand(command);
            }
        } catch (IOException e) {
            sendException(e);
        }
    }

    public static void readCommand(String command) {
        if (command == null) {
            sendWarn("未知指令！");
            return;
        }
        String[] args = command.split("\\s+");
        if (args.length < 1) {
            sendWarn("未知指令！");
            return;
        }
        switch (args[0]) {
            case "displayAPI":
                if (args.length < 3) {
                    sendWarn("指令参数错误！示例：displayAPI node true");
                } else {
                    ExecuteDisplayAPI.execute(args[1],args[2]);
                }
                break;
            case "status":
                ExecuteStatus.execute(BasicInfo.getRunType);
                break;
            case "stop":
                ExecuteStop.execute();
                break;
            default:
                sendWarn("未知指令！");
        }
    }
}
