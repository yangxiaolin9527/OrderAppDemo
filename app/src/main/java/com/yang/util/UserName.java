package com.yang.util;

public class UserName {
    private static String USE_NAME;

    public static String getUSE_NAME() {
        return USE_NAME;
    }

    public static void setUSE_NAME(String userName) {
        if (null == USE_NAME){
            USE_NAME = userName;
        }
    }
}
