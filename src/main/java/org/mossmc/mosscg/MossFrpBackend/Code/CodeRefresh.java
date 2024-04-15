package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlUpdate;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodePort;

import java.sql.SQLException;

public class CodeRefresh {
    public static void refresh(JSONObject data) throws Exception{
        refreshExecute(data.getString("node"),data.getString("port"),data.getString("ID"),data.getString("number"));
    }

    public static void refreshExecute(String node,String port,String ID,String number) {
        int newPort = NodePort.getEmptyPort(node,true);
        int oldPort = Integer.parseInt(port);

        String code = CodeEncode.encode(node, Integer.valueOf(number),newPort);

        CodeInfo.updateCodeInfo("ID",ID,"port",String.valueOf(newPort));
        CodeInfo.updateCodeInfo("ID",ID,"code",code);

        if (oldPort != 0) {
            NodePort.inputEmptyPort(node,oldPort,NodePort.portEmptyListMap);
        }
        NodeCache.nodeUpdate.add(node);
    }
}
