package org.mossmc.mosscg.MossFrpBackend.Web.Handler;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Info.InfoCheck;
import org.mossmc.mosscg.MossFrpBackend.Web.Request.*;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBasic;
import org.mossmc.mosscg.MossFrpBackend.Web.WebBlacklist;
import org.mossmc.mosscg.MossFrpBackend.Web.WebResponse;

import java.util.Random;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class HandlerClient implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            long receive = System.currentTimeMillis();
            WebResponse.initBasicResponse(exchange);
            String IP = WebBasic.getRemoteIP(exchange);
            WebBlacklist.addRequestCount(IP);
            JSONObject data = WebBasic.loadRequestData(exchange);
            JSONObject responseData = new JSONObject();
            data.put("remoteIP",IP);
            if (WebBlacklist.checkBlacklist(IP)) {
                RequestBlacklist.getReply(exchange,data,responseData);
                Thread.sleep(new Random().nextInt(5000)+1000);
                WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.CLIENT,receive);
                return;
            }
            if (!InfoCheck.checkData(data)) {
                RequestBadData.getReply(exchange,data,responseData);
                WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.CLIENT,receive);
                return;
            }
            switch (data.getOrDefault("type","null").toString()) {
                case "login":
                    RequestLogin.getReply(exchange,data,responseData);
                    break;
                case "userCode":
                    RequestUserCode.getReply(exchange,data,responseData);
                    break;
                case "allNode":
                    RequestAllNode.getReply(exchange,data,responseData);
                    break;
                case "createCode":
                    RequestCreateCode.getReply(exchange,data,responseData);
                    break;
                case "removeCode":
                    RequestRemoveCode.getReply(exchange,data,responseData);
                    break;
                case "verification":
                    RequestVerification.getReply(exchange,data,responseData);
                    break;
                case "register":
                    RequestRegister.getReply(exchange,data,responseData);
                    break;
                case "infoUpdate":
                    RequestInfoUpdate.getReply(exchange,data,responseData);
                    break;
                case "userInfo":
                    RequestUserInfo.getReply(exchange,data,responseData);
                    break;
                case "dateCode":
                    RequestDateCode.getReply(exchange,data,responseData);
                    break;
                case "bandCode":
                    RequestBandCode.getReply(exchange,data,responseData);
                    break;
                case "forgetPassword":
                    RequestForgetPassword.getReply(exchange,data,responseData);
                    break;
                case "statistic":
                    RequestStatistic.getReply(exchange,data,responseData);
                    break;
                case "luck":
                    RequestLuck.getReply(exchange,data,responseData);
                    break;
                case "signIn":
                    RequestSignIn.getReply(exchange,data,responseData);
                    break;
                case "cardUse":
                    RequestCardUse.getReply(exchange,data,responseData);
                    break;
                case "notice":
                    RequestNotice.getReply(exchange,data,responseData);
                    break;
                case "advertisement":
                    RequestAdvertisement.getReply(exchange,data,responseData);
                    break;
                case "shop":
                    RequestShop.getReply(exchange,data,responseData);
                    break;
                case "download":
                    RequestDownload.getReply(exchange,data,responseData);
                    break;
                default:
                    RequestUnknownType.getReply(exchange,data,responseData);
                    break;
            }
            WebResponse.completeResponse(exchange,responseData,data,Enums.typeAPI.CLIENT,receive);
        } catch (Exception e) {
            sendException(e);
        }
    }
}
