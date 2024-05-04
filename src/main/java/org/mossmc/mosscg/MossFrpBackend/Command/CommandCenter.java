package org.mossmc.mosscg.MossFrpBackend.Command;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Command.Execute.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class CommandCenter {
    public static void commandThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread commandListen = new Thread(CommandCenter::commandInput);
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
                System.out.println();
            }
        } catch (Exception e) {
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
            case "checkIP":
                if (args.length < 2) {
                    sendWarn("指令参数错误！示例：checkIP 3");
                } else {
                    ExecuteCheckIP.execute(args[1]);
                }
                break;
            case "checkCard":
                ExecuteCheckCard.execute();
                break;
            case "clearCache":
                ExecuteClearCache.execute();
                break;
            case "updateUser":
                if (args.length < 4) {
                    sendWarn("指令参数错误！示例：updateUser 10000000 username 墨守MossCG");
                } else {
                    ExecuteUpdateUser.execute(args[1],args[2],args[3]);
                }
                break;
            case "displayAPI":
                if (args.length < 3) {
                    sendWarn("指令参数错误！示例：displayAPI <node/client> true");
                } else {
                    ExecuteDisplayAPI.execute(args[1],args[2]);
                }
                break;
            case "createCard":
                if (args.length < 4) {
                    sendWarn("指令参数错误！示例：createCard 100 <silver/gold> #rd#100-500");
                } else {
                    ExecuteCreateCard.execute(args[1],args[2],args[3]);
                }
                break;
            case "createNode":
                if (args.length < 4) {
                    sendWarn("指令参数错误！示例：createNode sq1 宿迁BGP多线 40000-43000");
                } else {
                    ExecuteCreateNode.execute(args[1],args[2],args[3]);
                }
                break;
            case "removeIP":
                if (args.length < 2) {
                    sendWarn("指令参数错误！示例：removeIP 127.0.0.1");
                } else {
                    ExecuteRemoveIP.execute(args[1]);
                }
                break;
            case "createCode":
                if (args.length < 4) {
                    sendWarn("指令参数错误！示例：createCode sq1 30 3");
                } else {
                    ExecuteCreateCode.execute(args[1],args[2],args[3]);
                }
                break;
            case "status":
                ExecuteStatus.execute(BasicInfo.getRunType);
                break;
            case "userInfo":
                if (args.length < 3) {
                    sendWarn("指令参数错误！示例：userInfo qq 1292141077");
                } else {
                    ExecuteUserInfo.execute(args[1],args[2]);
                }
                break;
            case "test":
                if (args.length < 3) {
                    sendWarn("指令参数错误！示例：test abcTest 123");
                } else {
                    ExecuteTest.execute(args[1],args[2]);
                }
                break;
            case "botRestart":
                ExecuteBotRestart.execute();
                break;
            case "configReload":
                ExecuteConfigReload.execute();
                break;
            case "addIP":
                if (args.length < 4) {
                    //addIP blacklist add 58.48.32.154 FOREVER
                    sendWarn("指令参数错误！示例：addIP whitelist add 192.168.200.3 FOREVER");
                } else {
                    String dataString = null;
                    if (args.length >= 5) {
                        dataString = args[4];
                    }
                    ExecuteAddIP.execute(args[1],args[3],dataString,args[2]);
                }
                break;
            default:
                sendWarn("未知指令！");
        }
    }
}
