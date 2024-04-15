package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Bot.Reply.ReplyCodeDate;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeCreate;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Time.TimeAdd;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinTake;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;

import java.io.IOException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class RequestDateCode {
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
            String date = data.getString("date");
            if (node == null || date == null || number == null) {
                RequestWrongArgument.getReply(exchange, data, responseData);
                return;
            }

            String userID = TokenCache.tokenUserMap.get(data.getString("token"));
            String check = CodeInfoCheck.checkDate(node,number,"userID",userID,date);
            if (!check.equals("pass")) {
                setFailedData(responseData,check);
                return;
            }

            JSONObject codeData = CodeInfo.getCodeInfoByNodeNumber(node,number);
            assert codeData != null;
            int needCoin = codeData.getInteger("band")*Integer.parseInt(date)*Integer.parseInt(NodeCache.nodeCache.get(node).get("price"));
            Enums.coinType coinType = Enums.coinType.valueOf(NodeCache.nodeCache.get(node).get("coin").toUpperCase());
            String coinCheck = UserCoinTake.coinTake(coinType,needCoin,userID,"userID",NodeCache.nodeCache.get(node).get("provider"));
            if (!coinCheck.equals("pass")) {
                setFailedData(responseData,coinCheck);
                return;
            }
            assert userID != null;
            TimeAdd.dateAddDay(Integer.parseInt(date),node,number);
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
