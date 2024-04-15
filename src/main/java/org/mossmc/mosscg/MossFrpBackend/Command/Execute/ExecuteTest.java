package org.mossmc.mosscg.MossFrpBackend.Command.Execute;

import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptBase64;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMoss3;
import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptSHA;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender;
import org.mossmc.mosscg.MossFrpBackend.Mysql.MysqlGetResult;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.User.UserCoinAdd;
import org.mossmc.mosscg.MossFrpBackend.User.UserPassword;
import org.mossmc.mosscg.MossFrpBackend.Web.WebWhitelist;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class ExecuteTest {
    public static void execute(String key,String value) {
        switch (key) {
            case "newEncrypt":
                String pwd = value;
                pwd = EncryptSHA.encode(pwd,true);
                sendInfo(pwd);
                pwd = EncryptSHA.encode(pwd,true);
                sendInfo(pwd);
                pwd = EncryptBase64.encode(pwd);
                sendInfo(pwd);
                pwd = EncryptBase64.encode(pwd);
                sendInfo(pwd);
                break;
            case "pwdEncrypt":
                sendInfo(UserPassword.encryptPassword(value));
                break;
            case "nodeCache":
                sendInfo(NodeCache.nodeCache.toString());
                break;
            case "whitelistIP":
                if (value.equalsIgnoreCase("FOREVER")) {
                    sendInfo("永久白名单IP：");
                    WebWhitelist.whitelistIPForever.forEach(LoggerSender::sendInfo);
                } else {
                    sendInfo("临时白名单IP：");
                    WebWhitelist.whitelistIPTemp.forEach(LoggerSender::sendInfo);
                }
                break;
            case "URLDecoder":
                String result = null;
                if (value.contains("%")) {
                    try {
                        result = URLDecoder.decode(value,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        sendException(e);
                    }
                } else {
                    result = value;
                }
                sendInfo(result);
                break;
            case "coinAddAll":
                try {
                    int addAmount = Integer.parseInt(value);
                    String sql = "select * from user";
                    ResultSet set = MysqlGetResult.getResultSet(sql);
                    while (set.next()) {
                        String userID = set.getString("userID");
                        String userName = set.getString("username");
                        String email = set.getString("email");
                        UserCoinAdd.coinAdd(Enums.coinType.SILVER,addAmount,userID,"userID");
                        sendInfo("已为用户："+userID+"|"+userName+"|"+email+" 补偿了"+addAmount+"银币！");
                    }
                    sendInfo("complete");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendInfo("failed");
                }
                break;
            default:
                sendInfo("未知的测试内容！");
                break;
        }
    }
}
