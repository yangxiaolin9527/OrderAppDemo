package com.yang.util;

import okhttp3.OkHttpClient;

// 饿汉式单例模式
public class OkHttpClientUtil {
//    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
/*            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(),list);
                }

                @NonNull
                @Override
                public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();*/

    private OkHttpClientUtil() {
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

}
