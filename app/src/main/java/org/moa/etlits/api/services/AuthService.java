package org.moa.etlits.api.services;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AuthService {
   @GET("api/auth_check")
    Call<Void> login();
}
