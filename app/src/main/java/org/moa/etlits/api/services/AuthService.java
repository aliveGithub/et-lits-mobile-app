package org.moa.etlits.api.services;
import org.moa.etlits.api.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AuthService {
   @GET("api/auth_check")
    Call<Void> login();

    @GET("api/auth_check")
    Call<AuthResponse> login(@Header("Authorization") String authorization);
}
