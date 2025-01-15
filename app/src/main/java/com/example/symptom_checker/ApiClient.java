package com.example.symptom_checker;

import okhttp3.OkHttpClient;
import okhttp3.JavaNetCookieJar;
import java.net.CookieManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(new CookieManager()))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://sidd-w6r5.onrender.com")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}