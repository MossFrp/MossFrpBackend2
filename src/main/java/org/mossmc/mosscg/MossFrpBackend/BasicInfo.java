package org.mossmc.mosscg.MossFrpBackend;

import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;

import java.util.*;

public class BasicInfo {
    public static Enums.systemType getSystemType = Enums.systemType.WINDOWS;
    public static Enums.runType getRunType = Enums.runType.CENTER;

    public static boolean start = false;

    public static Long getTimeStart;

    public static Map<?,?> getConfigYaml;

    public static String getConfig(String path) {
        Object object = getConfigYaml.getOrDefault(path,null);
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    public static String getVersion = "V2.10.6.1.2142R";
    public static String getLightBadString = ";|!|*|$|#|<|>";
    public static String getBadString = ";|!|*|$|#|<|>|substr|declare|exec|master|drop|execute|select|update|and|delete|insert|truncate|char|into";

    public static void initBasicInfo(String[] args) {
        String argString = Arrays.toString(args);
        if (argString.contains("-runMode=Center")) {
            getRunType = Enums.runType.CENTER;
        } else {
            if (argString.contains("-runMode=Node")) {
                getRunType = Enums.runType.NODE;
            } else {
                LoggerSender.sendError("未知的运行模式！请添加启动参数：-runMode=Center或-runMode=Node");
                System.exit(1);
            }
        }

        String getSystemName = System.getProperty("os.name");
        if (!argString.contains("-systemType")) {
            if (getSystemName.toLowerCase().startsWith("linux")) {
                getSystemType = Enums.systemType.LINUX;
            }
            if (getSystemName.toLowerCase().startsWith("windows")) {
                getSystemType = Enums.systemType.WINDOWS;
            }
        } else {
            if (argString.contains("-systemType=Windows")) {
                getSystemType = Enums.systemType.WINDOWS;
            } else {
                if (argString.contains("-systemType=Linux")) {
                    getSystemType = Enums.systemType.LINUX;
                } else {
                    LoggerSender.sendError("未知的系统参数！请添加启动参数：-systemType=Windows或-systemType=Linux");
                    System.exit(1);
                }
            }
        }
    }

    public static String getCoinTypeName(Enums.coinType coinType) {
        if (coinType == null) {
            return "未知";
        }
        if (coinType.equals(Enums.coinType.SILVER)) {
            return "银币";
        }
        if (coinType.equals(Enums.coinType.GOLD)) {
            return "金币";
        }
        if (coinType.equals(Enums.coinType.MIXED)) {
            return "金币或银币";
        }
        return "未知";
    }

    public static int getUserLevelCode(String level) {
        if (level == null) {
            return 999;
        }
        if (level.equals("banned")) {
            return 900;
        }
        if (level.equals("default")) {
            return 600;
        }
        if (level.equals("provider")) {
            return 300;
        }
        if (level.equals("staff")) {
            return 100;
        }
        if (level.equals("admin")) {
            return 0;
        }
        return 999;
    }

    public static String getUserLevelName(String level) {
        if (level.equals("banned")) {
            return "封禁用户";
        }
        if (level.equals("default")) {
            return "普通用户";
        }
        if (level.equals("provider")) {
            return "节点提供者";
        }
        if (level.equals("staff")) {
            return "管理员";
        }
        if (level.equals("admin")) {
            return "站长";
        }
        return "未知";
    }

    public static String getCodeStatusName(String status) {
        if (status.equals("run")) {
            return "正常";
        }
        if (status.equals("banned")) {
            return "封禁";
        }
        if (status.equals("outdated")) {
            return "到期";
        }
        return "未知";
    }

    public static String getNodeStatusName(Enums.nodeStatusType status) {
        switch (status) {
            case ONLINE:
            case WAIT:
            case CHECK:
            case WARNING:
                return "在线";
            case OFFLINE:
                return "离线";
            default:
                return "未知";
        }
    }

    public static Random random = new Random();
    public static Integer getRandomInt(Integer rangeMax,Integer rangeMin) {

        return random.nextInt(rangeMax-rangeMin+1)+rangeMin;
    }

    public static String getRandomString(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int sub = random.nextInt(listRandomChar.length);
            code.append(listRandomChar[sub]);
        }
        return code.toString();
    }

    public static char[] listRandomChar = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    public static void checkThursday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int index=calendar.get(Calendar.DAY_OF_WEEK)-1;
        if (index == 4) {
            try {
                throw new ThursdayKFCVMe50Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*public static Boolean 墨守不是受 = true;
    public static void 墨守是受吗() {
        墨守不是受=true;
        if (墨守不是受) {
            System.out.println("好耶！");
            System.out.println("墨守不是受！");
        }
    }*/
}
