package com.royal.qna.service;

import com.royal.qna.service.ApiService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request requestWithHeaders = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .header("User-Agent", "Mozilla/5.0 (Android)")  // Add a User-Agent header
                                .build();
                        return chain.proceed(requestWithHeaders);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://springboot-qna-api.onrender.com/api/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}
