package com.example.tluofficehours.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static Context context;

    public static void init(Context appContext) {
        context = appContext;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Add authentication interceptor
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                
                // Get token from SharedPreferences
                String token = null;
                if (context != null) {
                    SharedPreferences prefs = context.getSharedPreferences("TLUOfficeHours", Context.MODE_PRIVATE);
                    token = prefs.getString("auth_token", null);
                    Log.d("RetrofitClient", "Token from SharedPreferences: " + (token != null ? "EXISTS" : "NULL"));
                }
                
                Request.Builder requestBuilder = original.newBuilder();
                
                // Add Authorization header if token exists
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                    Log.d("RetrofitClient", "Added Authorization header with token");
                    Log.d("RetrofitClient", "Request URL: " + original.url());
                    Log.d("RetrofitClient", "Authorization header: Bearer " + token.substring(0, Math.min(20, token.length())) + "...");
                } else {
                    Log.w("RetrofitClient", "No token found, request will be unauthenticated");
                    Log.w("RetrofitClient", "Request URL: " + original.url());
                }
                // Add Accept header for all requests
                requestBuilder.addHeader("Accept", "application/json");
                
                Request request = requestBuilder.build();
                return chain.proceed(request);
            };

            // Add interceptors to OkHttpClient
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(authInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}