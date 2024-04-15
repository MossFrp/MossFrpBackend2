package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeNumber;
import org.mossmc.mosscg.MossFrpBackend.Node.NodePort;

public class CodeCreate {
    public static String create(String node,String user,int band,int day) throws Exception {
        JSONObject data = new JSONObject();
        int port = NodePort.getEmptyPort(node, true);
        int number = NodeNumber.getEmptyNumber(node,true);
        long start = System.currentTimeMillis();
        long stop = start+1000L*60*60*24*day;
        boolean activity = Boolean.parseBoolean(NodeCache.nodeCache.get(node).get("activity"));
        String code = CodeEncode.encode(node,number,port);

        data.put("node", node);
        data.put("number", number);
        data.put("user", user);
        data.put("status", "run");
        data.put("activity", activity);
        data.put("warning", "false");
        data.put("band", band);
        data.put("port", port);
        data.put("code",code);
        data.put("start",start);
        data.put("stop",stop);

        MysqlInsert.insert("code",data);
        NodeCache.nodeUpdate.add(node);

        JSONObject codeData = CodeInfo.getCodeInfoByNodeNumber(node,String.valueOf(number));
        if (codeData == null) return null;
        return codeData.getString("ID");
    }
}
