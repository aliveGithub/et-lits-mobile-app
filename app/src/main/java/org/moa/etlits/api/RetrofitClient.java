package org.moa.etlits.api;

import org.moa.etlits.api.services.ConfigService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    //TODO: move to settings
    private static final String BASE_URL = "https://lits.dgstg.org/lits-mobile-api/";

    private static OkHttpClient HTTP_CLIENT;

    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private static OkHttpClient createHttpClient(String username, String password) {
        if (HTTP_CLIENT == null) {
            HTTP_CLIENT = new OkHttpClient();
        }

        return HTTP_CLIENT.newBuilder()
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
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ConfigService getConfigService(String username, String password) {
        Retrofit retrofit = createRetrofit(username, password);
        ConfigService apiService = retrofit.create(ConfigService.class);
        return apiService;
    }
}
