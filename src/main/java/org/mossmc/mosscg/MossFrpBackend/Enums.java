package org.mossmc.mosscg.MossFrpBackend;

public class Enums {
    public enum timeStringType{
        FULL,DATE,TIME,LOG
    }

    public enum infoType{
        INFO,WARN,ERROR,EXCEPTION,API,COMMAND,CARD
    }

    public enum runType{
        CENTER,NODE
    }

    public enum systemType{
        WINDOWS,LINUX
    }

    public enum typeAPI{
        CLIENT,NODE
    }

    public enum mailType{
        VERIFICATION,ONE,OUTDATED,REMOVED,CODE,BANNED_USER,BANNED_CODE,UNBANNED_USER,UNBANNED_CODE,AD_PASS,AD_REJECT,AD_OUTDATED
    }

    public enum nodeStatusType{
        ONLINE,WAIT,CHECK,WARNING,OFFLINE
    }

    public enum coinType{
        GOLD,SILVER,MIXED
    }

    public enum whitelistType{
        FOREVER,TEMP
    }

    public enum noticeType{
        SUPER,TOP,NORMAL
    }
}
