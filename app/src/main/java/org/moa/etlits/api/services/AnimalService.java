package org.moa.etlits.api.services;

import org.moa.etlits.api.request.AnimalRegRequest;
import org.moa.etlits.api.response.AnimalRegResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AnimalService {
   @POST("api/animal/register")
    Call<AnimalRegResponse> registerAnimals(@Header("Authorization") String authorization, @Body AnimalRegRequest animalRegRequest);
}
