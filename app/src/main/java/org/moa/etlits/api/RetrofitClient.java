package org.moa.etlits.api;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.Credentials;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static final String BASE_URL = "https://lits.dgstg.org/lits-mobile-api/";
    private static Retrofit getRetrofit(String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("Authorization", Credentials.basic(username, password))
                            .method(original.method(), original.body());

                    return chain.proceed(builder.build());
                })
                .build();

          return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void getConfigService(String username, String password) {
        Retrofit retrofit = getRetrofit(username, password);
        ConfigService apiService = retrofit.create(ConfigService.class);
        Call call = apiService.getConfigData();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("TAG", "response: "+ new Gson().toJson(response.body()) );
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("TAG", "onFailure: "+t.toString() );
                // Log error here since request failed
            }
        });

    }
}
