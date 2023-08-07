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

    private static OkHttpClient createHttpClient(String username, String password) {
       /* if (HTTP_CLIENT == null) {
            HTTP_CLIENT = new OkHttpClient();
        }*/

        /*TODO: create new client - okHttp perform best if connections are reused.
        There was an issue with the reused client. If authentication fails the first time, subsequent attempts fail even if the credentials are correct.
        We need to investigate the issue and reuse the client.*/

        OkHttpClient client = new OkHttpClient();
        return client.newBuilder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("Authorization", Credentials.basic(username, password))
                            .method(original.method(), original.body());

                    return chain.proceed(builder.build());
                }).cookieJar(new CookieJar() {
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

    private static Retrofit createRetrofit(String username, String password) {
        OkHttpClient client = createHttpClient(username, password);
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(callBackExecutors)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createAPI(Class<T> clazz, String username, String password) {
        Retrofit retrofit = createRetrofit(username, password);
        return retrofit.create(clazz);
    }

    public static ConfigService createCofigService(String username, String password) {
        return createAPI(ConfigService.class, username, password);
    }

    public static AuthService createAuthService(String username, String password) {
        return createAPI(AuthService.class, username, password);
    }
}
