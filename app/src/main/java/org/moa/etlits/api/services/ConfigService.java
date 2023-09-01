package org.moa.etlits.api.services;
import org.moa.etlits.api.response.ConfigResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ConfigService {
   @GET("api/config")
    Call<ConfigResponse> getConfigData(@Header("Authorization") String authorization);
}
