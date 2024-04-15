package org.mossmc.mosscg.MossFrpBackend.Encrypt;

import java.math.BigInteger;
import java.security.MessageDigest;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class EncryptMD {
    public static String encode(String inputStr) {
        BigInteger bigInteger;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStr.getBytes());
            bigInteger = new BigInteger(md.digest());
            return bigInteger.toString();
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }

    public static String oldMd5(String inputStr) {
        BigInteger bigInteger;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStr.getBytes());
            bigInteger = new BigInteger(md.digest());
            return bigInteger.toString();
        } catch (Exception e) {
            sendException(e);
            return null;
        }
    }
}
