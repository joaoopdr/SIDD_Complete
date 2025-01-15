package com.example.symptom_checker;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SymptomCheckerApi {
    @GET("/start")
    Call<ApiResponse> startConversation();

    @POST("/continue")
    Call<ApiResponse> continueConversation(@Body RequestBody requestBody);
}