package org.mossmc.mosscg.MossFrpBackend.Encrypt;

import cn.hutool.crypto.digest.DigestUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class EncryptSHA {
    public static String encode(String inputStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(inputStr.getBytes(StandardCharsets.UTF_8));
            byte[] result = md.digest();
            StringBuilder res = new StringBuilder();
            for (byte b : result) {
                res.append(String.format("%02X", b));
            }
            return res.toString();
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static String encode(String inputStr,boolean useTool) {
        try {
            if (useTool) {
                return DigestUtil.sha256Hex(inputStr);
            } else {
                return encode(inputStr);
            }
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }
}
