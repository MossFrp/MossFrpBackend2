package org.mossmc.mosscg.MossFrpBackend.Encrypt;

import java.util.Base64;

public class EncryptBase64 {
    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
    public static String decode(String input) {
        byte[] decodeArray = Base64.getDecoder().decode(input);
        return new String(decodeArray);
    }
}
