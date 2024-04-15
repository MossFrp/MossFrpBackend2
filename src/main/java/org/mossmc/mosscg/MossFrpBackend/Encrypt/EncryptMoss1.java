package org.mossmc.mosscg.MossFrpBackend.Encrypt;

public class EncryptMoss1 {
    public static String encode(String input) {
        //version 1
        String base64String = EncryptBase64.encode(input);
        base64String = EncryptBase64.encode(base64String);
        int i = 0;
        StringBuilder output = new StringBuilder("Moss1$");
        while (i < base64String.length()) {
            output.append(base64String.charAt(i));
            i = i + 3;
        }
        output.append("l").append(input.length()).append("L").append(base64String.length());
        return output.toString();
    }
}
