package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Code.CodeInfo;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenGet;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestUserCode {
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
            JSONObject codeData = CodeInfo.getUserCodeInfo(TokenCache.tokenUserMap.get(data.getString("token")));
            if (data.containsKey("getAsList") && data.getString("getAsList").equals("true")) {
                List<JSONObject> dataList = new ArrayList<>();
                if (codeData != null) {
                    codeData.forEach((codeNumber, dataCode) -> {
                        dataList.add(JSONObject.parseObject(dataCode.toString()));
                    });
                }
                responseData.put("codeData",dataList);
            } else {
                List<String> codeList = new ArrayList<>();
                if (codeData != null) {
                    codeData.forEach((codeNumber, dataCode) -> codeList.add(codeNumber));
                }
                responseData.put("codeData",codeData);
                responseData.put("codeList",codeList);
            }
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");

    }
}
