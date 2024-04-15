package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class CodeDecode {
    public static JSONObject decode(String code) {
        JSONObject output = new JSONObject();
        String node = "";
        int randomInt = 0;
        int port = 0;
        int frpNumber = 0;
        int nodeLength;
        try {
            nodeLength = Integer.parseInt(code.substring(0,1));
            node = code.substring(1,1+nodeLength);
            randomInt = Integer.parseInt(code.substring(1+nodeLength,6+nodeLength));
            port = Integer.parseInt(code.substring(6+nodeLength,11+nodeLength))-randomInt;
            frpNumber = Integer.parseInt(code.substring(11+nodeLength,18+nodeLength))-randomInt;
        }catch (Exception e) {
            sendException(e);
            output.put("code",code);
            output.put("random",String.valueOf(randomInt));
            output.put("port",String.valueOf(port));
            output.put("number",String.valueOf(frpNumber));
            output.put("node",node);
            output.put("status","failed");
            return output;
        }
        output.put("code",code); //穿透码本身，同时为token
        output.put("random",String.valueOf(randomInt)); //随机数，参考意义不大
        output.put("port",String.valueOf(port)); //frps端口，一般是xxxx0，后续9位都是开放端口如12340->12341-12349
        output.put("number",String.valueOf(frpNumber)); //穿透码编号
        output.put("node",node); //如nj1，服务端地址即为nj1.mossfrp.cn
        output.put("status","succeed"); //是否成功
        return output;
    }
}
