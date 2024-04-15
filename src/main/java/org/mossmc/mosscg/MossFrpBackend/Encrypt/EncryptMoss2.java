package org.mossmc.mosscg.MossFrpBackend.Encrypt;

public class EncryptMoss2 {
    public static String encode(String input) {
        //version 2
        String MDString = EncryptMD.encode(input);
        if (MDString == null) {
            return null;
        }
        String base64String = EncryptBase64.encode(MDString);
        base64String = EncryptBase64.encode(base64String);
        return "Moss2$"+base64String+"l"+input.length()+"L"+base64String.length();
    }

    public static String encode(String input,boolean encrypted) {
        String result;
        if (encrypted) {
            String base64String = EncryptBase64.encode(input);
            base64String = EncryptBase64.encode(base64String);
            result = "Moss2$"+base64String+"l"+input.length()+"L"+base64String.length();
        } else {
            result = encode(input);
        }
        return result;
    }
}
