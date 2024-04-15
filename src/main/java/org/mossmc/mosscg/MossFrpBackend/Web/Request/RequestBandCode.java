package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeAdd;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeRemain;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinTake;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class RequestBandCode {
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
            String band = data.getString("band");
            if (node == null || band == null || number == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }

            String userID = TokenCache.tokenUserMap.get(data.getString("token"));
            String check = CodeInfoCheck.checkBand(node,number,"userID",userID,band);
            if (!check.equals("pass")) {
                setFailedData(responseData,check);
                return;
            }

            JSONObject codeData = CodeInfo.getCodeInfoByNodeNumber(node,number);
            assert codeData != null;
            int remain = TimeRemain.dateRemainDay(node,number);
            int newBand = codeData.getInteger("band")+Integer.parseInt(band);
            int needCoin = Integer.parseInt(band)*remain*Integer.parseInt(NodeCache.nodeCache.get(node).get("price"));
            Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(node).get("coin").toUpperCase());
            String coinCheck = UserCoinTake.coinTake(coinType,needCoin,userID,"userID",NodeCache.nodeCache.get(node).get("provider"));
            if (!coinCheck.equals("pass")) {
                setFailedData(responseData,coinCheck);
                return;
            }
            assert userID != null;
            CodeInfo.updateCodeInfo("ID",codeData.getString("ID"),"band",String.valueOf(newBand));
            if (NodeCache.nodeCache.get(node).get("activity").equals("true")) {
                CodeInfo.updateCodeInfo("ID",codeData.getString("ID"),"activity","true");
            }
            setSuccessData(responseData,needCoin,coinType);
        } catch (Exception e) {
            sendException(e);
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData,int coin,Enums.coinType type) {
        responseData.put("status","200");
        responseData.put("coin",coin);
        responseData.put("type",type.name());
    }

    public static void setFailedData(JSONObject responseData,String reason) {
        responseData.put("status","403");
        responseData.put("message",reason);
    }
}
