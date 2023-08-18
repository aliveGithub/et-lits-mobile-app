package org.moa.etlits.api.services;
import org.moa.etlits.api.ConfigResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfigService {
   @GET("api/config")
    Call<ConfigResponse> getConfigData();
}
