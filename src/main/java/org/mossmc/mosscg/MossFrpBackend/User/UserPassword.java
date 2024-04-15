package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.Encrypt.EncryptMain;

import java.util.ArrayList;
import java.util.List;

public class UserPassword {
    public static List<String> easyPassword;
    public static void inputEasyPassword() {
        easyPassword = new ArrayList<>();
        easyPassword.add("123456");
        easyPassword.add("12345678");
        easyPassword.add("88888888");
        easyPassword.add("66666666");
        easyPassword.add("password");
    }

    public static boolean checkPassword(String password) {
        if (easyPassword.contains(password)) {
            return false;
        }
        if (password.length() > 32||password.length() < 6) {
            return false;
        }

        return true;
    }

    public static String encryptPassword(String password) {
        return EncryptMain.encode(password);
    }
}
