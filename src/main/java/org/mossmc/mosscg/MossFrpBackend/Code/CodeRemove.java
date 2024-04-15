package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlDelete;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeNumber;
import org.mossmc.mosscg.MossFrpBackend.Node.NodePort;

import java.sql.SQLException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class CodeRemove {
    public static void remove(JSONObject data) {
        try {
            if (data == null) {
                return;
            }
            NodeNumber.releaseNumber(data.getString("node"),data.getInteger("number"));
            NodePort.inputEmptyPort(data.getString("node"),data.getInteger("port"),NodePort.portEmptyListMap);
            MysqlDelete.execute("code","ID",data.getString("ID"));
            NodeCache.nodeUpdate.add(data.getString("node"));
        } catch (SQLException e) {
            sendException(e);
        }
    }
}
