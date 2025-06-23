package com.example.tluofficehours.api;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
        // Reset retrofit instance to apply new token
        retrofit = null;
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            android.util.Log.d("RetrofitClient", "Creating new Retrofit instance. Current token: " + (authToken != null ? authToken : "null"));
            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create auth interceptor
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                
                if (authToken != null && !authToken.isEmpty()) {
                    builder.addHeader("Authorization", "Bearer " + authToken);
                    android.util.Log.d("RetrofitClient", "Adding Authorization header: Bearer " + authToken);
                } else {
                    android.util.Log.w("RetrofitClient", "No auth token available for request: " + original.url());
                }
                android.util.Log.d("RetrofitClient", "Request URL: " + original.url());
                android.util.Log.d("RetrofitClient", "Request Headers: " + builder.build().headers());
                Request request = builder.build();
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
        return retrofit.create(ApiService.class);
    }
}