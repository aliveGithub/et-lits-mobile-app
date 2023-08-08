package org.moa.etlits.api;

import org.moa.etlits.api.services.AuthService;
import org.moa.etlits.api.services.ConfigService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtil {
    //TODO: move to settings
    private static final String BASE_URL = "https://lits.dgstg.org/lits-mobile-api/";

    private static OkHttpClient HTTP_CLIENT;

    public static final ExecutorService callBackExecutors =
            Executors.newFixedThreadPool(4);
    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private static OkHttpClient createHttpClient() {
        if (HTTP_CLIENT == null) {
            HTTP_CLIENT = new OkHttpClient();
        }

       return HTTP_CLIENT.newBuilder()
               .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
                        cookieStore.put(httpUrl.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
    }

    private static Retrofit createRetrofit() {
        OkHttpClient client = createHttpClient();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(callBackExecutors)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createAPI(Class<T> clazz) {
        Retrofit retrofit = createRetrofit();
        return retrofit.create(clazz);
    }

    public static ConfigService createCofigService() {
        return createAPI(ConfigService.class);
    }

    public static AuthService createAuthService() {
        return createAPI(AuthService.class);
    }
}
