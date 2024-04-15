package org.mossmc.mosscg.MossFrpBackend.User;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class UserInfoCheck {

    //registerType参数：qq/web
    public static String checkRegister(String email,String password,String name,String number,String registerType) {
        try {
            String checkPasswordResult = checkPassword(password);
            if (!checkPasswordResult.equals("pass")) {
                return checkPasswordResult;
            }
            String checkEmailResult = checkEmail(email);
            if (!checkEmailResult.equals("pass")) {
                return checkEmailResult;
            }
            String checkEmailRegisterResult = checkEmailRegister(email);
            if (!checkEmailRegisterResult.equals("pass")) {
                return checkEmailRegisterResult;
            }
            String checkNameResult = checkName(name);
            if (!checkNameResult.equals("pass")) {
                return checkNameResult;
            }
            if (registerType.equals("qq")) {
                String checkQQResult = checkQQ(number);
                if (!checkQQResult.equals("pass")) {
                    return checkQQResult;
                }
            }
        } catch (Exception e) {
            sendException(e);
            return "查询是否符合注册条件时失败！注册失败！请联系管理员！";
        }
        return "pass";
    }

    public static String checkEmail(String email) {
        try {
            if (email == null) {
                return "请填写正确的邮箱地址！";
            }
            if (!email.contains("@")) {
                return "请填写正确的邮箱地址！";
            }
            if (email.length() > 64 || email.length() < 4) {
                return "邮箱长度应小于等于64位且大于等于4位";
            }
            String[] cut = BasicInfo.getLightBadString.split("\\|");
            for (String s : cut) {
                if (email.toLowerCase().contains(s)) {
                    return "邮箱信息中含有非法字符：" + s + "！";
                }
            }
        } catch (Exception e) {
            sendException(e);
            return "查询出错！请重试或反馈错误！";
        }
        return "pass";
    }

    public static String checkEmailRegister(String email) {
        if (UserCache.emailCache.contains(email)) {
            return "邮箱已被注册！请更换邮箱！";
        }
        return "pass";
    }

    public static String checkQQ(String qq) {
        try {
            try {
                Long.parseLong(qq);
            } catch (Exception e) {
                return "非法的QQ号！";
            }
            if (UserCache.qqCache.contains(qq)) {
                return "该QQ已被注册！请更换！";
            }
        } catch (Exception e) {
            sendException(e);
            return "查询出错！请重试或反馈错误！";
        }
        return "pass";
    }

    public static String checkPassword(String password) {
        try {
            if (password.length() < 6 || password.length() > 32) {
                return "密码长度不合规，请设置6-32位的密码！";
            }
            if (UserPassword.easyPassword.contains(password)) {
                return "你设置的密码太简单了！请更换！";
            }
            String[] cut = BasicInfo.getLightBadString.split("\\|");
            for (String part : cut) {
                if (password.toLowerCase().contains(part)) {
                    return "您的密码中存在非法字符："+part;
                }
            }
        } catch (Exception e) {
            sendException(e);
            return "查询出错！请重试或反馈错误！";
        }
        return "pass";
    }

    public static String checkName(String username) {
        if (username.length() > 32 || username.length() < 4) {
            return "名称长度不合规，请设置4-32位的名称！";
        }
        try {
            String[] cut = BasicInfo.getLightBadString.split("\\|");
            for (String s : cut) {
                if (username.toLowerCase().contains(s)) {
                    return "昵称信息中含有非法字符：" + s + "！";
                }
            }
            if (UserCache.nameCache.contains(username)) {
                return "昵称已被注册！请更换昵称！";
            }
        } catch (Exception e) {
            sendException(e);
            return "查询出错！请重试或反馈错误！";
        }
        return "pass";
    }
}
