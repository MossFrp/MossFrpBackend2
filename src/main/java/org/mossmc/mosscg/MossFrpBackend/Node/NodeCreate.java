package org.mossmc.mosscg.MossFrpBackend.Node;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlInsert;

import java.sql.SQLException;
import java.util.Random;

public class NodeCreate {
    public static void create(String nodeCode,String nodeAddress,String port) {
        JSONObject nodeData = new JSONObject();
        try {
            nodeData.put("node",nodeCode);
            nodeData.put("address",nodeAddress);
            nodeData.put("password", getRandomAuth());
            nodeData.put("port", port);
            MysqlInsert.insert("node", nodeData);
            NodeCache.refreshCache();
            NodePort.loadEmptyPort();
            NodeNumber.loadUsedNumber();
            NodeStatic.updateStatic();
        } catch (SQLException e) {
            LoggerSender.sendException(e);
        }
    }

    public static String getRandomAuth() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 32; i++) {
            int sub = random.nextInt(listRandomChar.length);
            code.append(listRandomChar[sub]);
        }
        return code.toString();
    }

    public static char[] listRandomChar = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
}
