package org.mossmc.mosscg.MossFrpBackend.Info;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class InfoGroup {
    public static void copyright() {
        sendWarn("版权声明：MossFrpBackend后端版权为墨守MossCG所有，未经允许，严禁转载与二次发布！");
        sendWarn("请勿将此软件用于其它的任何商业/非商业用途！违者必究！");
    }

    public static void logo() {
        sendInfo("#############################################################");
        sendInfo("#                                                           #");
        sendInfo("#    # #     ####   ######  ######  ######  ######  ######  #");
        sendInfo("#   ## ##   #    #  #       #       #       #    #  #    #  #");
        sendInfo("#  # # # #  #    #  ######  ######  ######  ######  ######  #");
        sendInfo("#  #  #  #  #    #       #       #  #       # ##    #       #");
        sendInfo("#  #  #  #   ####   ######  ######  #       #   ##  #       #");
        sendInfo("#                                                           #");
        sendInfo("#############################################################");
    }

    public static void runInfo() {
        sendInfo("系统类型："+System.getProperty("os.name")+" - "+ BasicInfo.getSystemType.name());
        sendInfo("运行模式："+BasicInfo.getRunType.name());
        sendInfo("软件版本："+BasicInfo.getVersion);
    }
}
