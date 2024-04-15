package org.mossmc.mosscg.MossFrpBackend.Web.Request;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoNotice;
import org.mossmc.mosscg.MossFrpBackend.User.UserPermission;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCache;
import org.mossmc.mosscg.MossFrpBackend.Web.Token.TokenCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.WebAdvertisement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class RequestAdvertisement {
    //此处具体使用见API文档
    public static void getReply(HttpExchange exchange, JSONObject data, JSONObject responseData) throws IOException {
        try {
            int adCount = 3;
            List<JSONObject> dataADs = new ArrayList<>();
            for (int i = 0; i < adCount; i++) {
                dataADs.add(WebAdvertisement.getAD());
            }
            responseData.put("advertisementData",dataADs);
            setSuccessData(responseData);
        } catch (Exception e) {
            RequestServerError.getReply(exchange, data, responseData);
        }
    }

    public static void setSuccessData(JSONObject responseData) {
        responseData.put("status","200");
    }
}
