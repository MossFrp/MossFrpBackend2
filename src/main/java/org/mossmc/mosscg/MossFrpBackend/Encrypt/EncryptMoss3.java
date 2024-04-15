package org.mossmc.mosscg.MossFrpBackend.Encrypt;

public class EncryptMoss3 {
    public static String encode(String input) {
        //version 3
        String SHA256String = EncryptSHA.encode(input,true);
        if (SHA256String == null) {
            return null;
        }
        SHA256String = EncryptSHA.encode(SHA256String,true);
        if (SHA256String == null) {
            return null;
        }
        String base64String = EncryptBase64.encode(SHA256String);
        base64String = EncryptBase64.encode(base64String);
        return "Moss3$"+base64String;
    }

    public static String encode(String input, boolean encrypted) {
        String result;
        if (encrypted) {
            String SHA256String = EncryptSHA.encode(input,true);
            assert SHA256String != null;
            String base64String = EncryptBase64.encode(SHA256String);
            base64String = EncryptBase64.encode(base64String);
            result = "Moss3$"+base64String;
        } else {
            result = encode(input);
        }
        return result;
    }
}
