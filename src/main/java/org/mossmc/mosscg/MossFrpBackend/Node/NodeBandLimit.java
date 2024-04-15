package org.mossmc.mosscg.MossFrpBackend.Node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getSystemType;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class NodeBandLimit {
    public static List<String> emptyClassIDList = new ArrayList<>();
    public static Map<String,String> IDClassIDMap = new HashMap<>();
    public static boolean asJob = false;
    public static int getLimitNumber(int band) {
        return band*128000;
    }

    public static void setLimit(int ID, int band,int port) {
        sendInfo("正在为"+ ID +"设置带宽限制");
        executeLimit(ID,band,port);
        sendInfo("设置带宽限制完成");
    }

    public static void executeLimit(int ID, int band, int port) {
        try {
            switch (getSystemType) {
                case WINDOWS:
                    String command = "powershell.exe New-NetQosPolicy -Name \"frps_"+ ID +"\" -ThrottleRate "+getLimitNumber(band)+" -AppPathName \"frps_"+ ID +".exe\" -IPProtocol \"Both\"";
                    Process powerShellProcess = Runtime.getRuntime().exec(command);
                    BufferedReader powershellOutput = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
                    BufferedReader powershellError = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
                    String powershellInfo = powershellOutput.readLine();
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellOutput.readLine();
                    }
                    powershellInfo = "";
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellError.readLine();
                    }
                    powerShellProcess.waitFor();
                    powerShellProcess.destroy();
                    powershellOutput.close();
                    break;
                case LINUX:
                    String classID = getEmptyClassID(String.valueOf(ID));
                    String eth = getConfig("ethName");
                    executeLinuxCommand("tc class add dev "+eth+" parent 1:5 classid 1:"+classID+" htb rate "+band+"mbit ceil "+band+"mbit");
                    for (int i = port;i <= port+9;i++) {
                        executeLinuxCommand("tc filter add dev "+eth+" protocol ip parent 1:0 prio 1 u32 match ip sport "+i+" 0xffff flowid 1:"+classID);
                    }
                    for (int i = port;i <= port+9;i++) {
                        executeLinuxCommand("tc filter add dev "+eth+" protocol ip parent 1:0 prio 1 u32 match ip dport "+i+" 0xffff flowid 1:"+classID);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void removeLimit(String ID) {
        try {
            switch (getSystemType) {
                case WINDOWS:
                    Process powerShellProcess;
                    if (asJob) {
                        powerShellProcess = Runtime.getRuntime().exec("powershell.exe Remove-NetQosPolicy -Name \"frps_"+ID+"\" -AsJob\n");
                        Thread.sleep(1000);
                    } else {
                        powerShellProcess = Runtime.getRuntime().exec("powershell.exe Remove-NetQosPolicy -Name \"frps_"+ID+"\"\n");
                        powerShellProcess.getOutputStream().write("A\n".getBytes());
                        powerShellProcess.getOutputStream().flush();
                        powerShellProcess.getOutputStream().close();
                    }
                    BufferedReader powershellOutput = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
                    BufferedReader powershellError = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
                    powershellOutput.readLine();
                    String powershellInfo = powershellOutput.readLine();
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellOutput.readLine();
                    }
                    powershellInfo = "";
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellError.readLine();
                    }
                    powerShellProcess.waitFor();
                    powerShellProcess.destroy();
                    powershellOutput.close();
                    break;
                case LINUX:
                    String classID = IDClassIDMap.get(ID);
                    String eth = getConfig("ethName");
                    while (true) {
                        String handler = getHandler(classID);
                        if (handler == null) {
                            break;
                        }
                        executeLinuxCommand("tc filter del dev "+eth+" parent 1: handle "+handler+" prio 1 protocol ip u32");
                    }
                    executeLinuxCommand("tc class del dev "+eth+" classid 1:"+classID);
                    putEmptyClassID(ID);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void initLimit() {
        try {
            switch (getSystemType) {
                case WINDOWS:
                    String asJobString = getConfig("powershellAsJob");
                    if (!asJobString.equals("")) {
                        if (asJobString.equals("true")) {
                            asJob = true;
                        }
                    }
                    Process powerShellProcess;
                    if (asJob) {
                        powerShellProcess = Runtime.getRuntime().exec("powershell.exe Remove-NetQosPolicy -AsJob\n");
                        Thread.sleep(10000);
                    } else {
                        powerShellProcess = Runtime.getRuntime().exec("powershell.exe Remove-NetQosPolicy\r\n");
                        powerShellProcess.getOutputStream().write("A\r\n".getBytes(StandardCharsets.UTF_8));
                        powerShellProcess.getOutputStream().flush();
                        powerShellProcess.getOutputStream().close();
                    }
                    BufferedReader powershellOutput = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
                    BufferedReader powershellError = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
                    if (!asJob) {
                        powershellOutput.readLine();
                    }
                    String powershellInfo = powershellOutput.readLine();
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellOutput.readLine();
                    }
                    powershellInfo = powershellError.readLine();
                    while (powershellInfo != null) {
                        sendInfo(powershellInfo);
                        powershellInfo = powershellError.readLine();
                    }
                    powerShellProcess.destroy();
                    powershellOutput.close();
                    break;
                case LINUX:
                    String eth = getConfig("ethName");
                    executeLinuxCommand("tc qdisc del dev "+eth+" root");
                    executeLinuxCommand("tc qdisc add dev "+eth+" root handle 1: htb default 5");
                    executeLinuxCommand("tc class add dev "+eth+" parent 1: classid 1:5 htb rate 10000mbps ceil 10000mbps");
                    for (int i = 100;i <= 1000;i++) {
                        emptyClassIDList.add(String.valueOf(i));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static String getEmptyClassID(String ID) {
        String get = emptyClassIDList.get(1);
        emptyClassIDList.remove(get);
        IDClassIDMap.put(ID,get);
        return get;
    }

    public static void putEmptyClassID(String ID) {
        emptyClassIDList.add(IDClassIDMap.get(ID));
        IDClassIDMap.remove(ID);
    }

    public static void executeLinuxCommand(String command) throws Exception {
        String message = "";
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while (message != null) {
            message = output.readLine();
            if (message != null) {
                sendInfo(message);
            }
        }
        message = "";
        while (message != null) {
            message = stderr.readLine();
            if (message != null) {
                sendInfo(message);
            }
        }
        output.close();
        process.destroy();
    }

    public static String getHandler(String classID) throws Exception {
        String eth = getConfig("ethName");
        String handler = null;
        String command = "tc filter list dev "+eth;
        Process process = Runtime.getRuntime().exec(command);
        sendInfo(command);

        BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String info = output.readLine();
        while (info != null) {
            //sendInfo(info);
            if (info.contains("flowid 1:"+classID)) {
                for (String cut : info.split("\\s+")) {
                    if (cut.contains("::")) {
                        handler = cut;
                    }
                }
            }
            info = output.readLine();
        }

        output = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        info = output.readLine();
        while (info != null) {
            sendInfo(info);
            info = output.readLine();
        }

        process.destroy();
        output.close();
        return handler;
    }
}
