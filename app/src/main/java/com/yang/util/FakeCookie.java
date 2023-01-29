package com.yang.util;

public class FakeCookie {
    private static String FAKE_COOKIE;

    public static String getFakeCookie() {
        return FAKE_COOKIE;
    }

    public static void setFakeCookie(String fakeCookie) {
        if(null == FAKE_COOKIE){
            FAKE_COOKIE = fakeCookie;
        }
    }
}
