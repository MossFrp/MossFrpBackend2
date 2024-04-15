package org.mossmc.mosscg.MossFrpBackend.Encrypt;

public class EncryptMain {
    public static String encode(String input) {
        return EncryptMoss3.encode(input);
    }
    public static String encode(String input,boolean encrypted) {
        return EncryptMoss3.encode(input,encrypted);
    }
}
