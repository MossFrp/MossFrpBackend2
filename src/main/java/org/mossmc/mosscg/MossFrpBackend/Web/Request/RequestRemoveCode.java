package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeRemove;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeRemain;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinAdd;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class RequestRemoveCode {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            if (!TokenCheck.checkClient(data.getString("token"),data.getString("remoteIP"))) {
                RequestWrongToken.getReply(exchange, data, responseData);
                return;
            }
            int permission = UserPermission.getPermission(TokenCache.tokenUserMap.get(data.getString("token")));
            if (permission > 600) {
                RequestNoPermission.getReply(exchange, data, responseData);
                return;
            }
            String node = data.getString("node");
            String number = data.getString("number");
            if (node == null || number == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }
            String userID = TokenCache.tokenUserMap.get(data.getString("token"));
            String check = CodeInfoCheck.checkRemove(node,number,"userID",userID);
            if (!check.equals("pass")) {
                setFailedData(responseData,check);
                return;
            }
            JSONObject codeInfo = CodeInfo.getCodeInfoByNodeNumber(node,number);
            assert codeInfo != null;
            int band = codeInfo.getInteger("band");
            int dateRemain = TimeRemain.dateRemainDay(node,number);
            int price = Integer.parseInt(NodeCache.nodeCache.get(node).get("price"));
            int returnCoin = (dateRemain-1)*price*band;
            CodeRemove.remove(codeInfo);
            if (returnCoin > 0) {
                UserCoinAdd.coinAdd(Enums.coinType.SILVER, returnCoin, userID, "userID");
            } else {
                returnCoin = 0;
            }
            setSuccessData(responseData,returnCoin);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData,int coin) {
        responseData.put("status","200");
        responseData.put("coin",coin);
    }

    public static void setFailedData(JSONObject responseData,String reason) {
        responseData.put("status","403");
        responseData.put("message",reason);
    }
}
