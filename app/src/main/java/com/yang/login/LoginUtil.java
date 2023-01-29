package com.yang.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUtil {

    static int loginRes;

    public static boolean isPhoneValid(String account) {
        if (null == account) {
            return false;
        }
        String pattern = "1\\d{10}";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(account);
        return matcher.matches();
    }

    public static boolean isPswValid(String psw) {
        return psw != null && psw.trim().length() > 5;
    }

}
