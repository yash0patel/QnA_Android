package com.royal.qna.service;

import com.royal.qna.model.LoginRequestModel;
import com.royal.qna.model.LoginResponseModel;
import com.royal.qna.model.UserRequestModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<Void> createUser(@Body UserRequestModel userRequestModel);

    @POST("login")
    Call<LoginResponseModel> loginUser(
            @HeaderMap Map<String, String> headers,
            @Body LoginRequestModel loginRequest
    );

}
