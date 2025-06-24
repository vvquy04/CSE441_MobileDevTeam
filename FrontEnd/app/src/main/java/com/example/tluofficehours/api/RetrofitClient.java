package com.example.tluofficehours.api;

// HEAD
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
//
// vanquy_refactor
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
// HEAD
    private static Context context;
//
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
        // Reset retrofit instance to apply new token
        retrofit = null;
    }
// vanquy_refactor

    public static void init(Context appContext) {
        context = appContext;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            android.util.Log.d("RetrofitClient", "Creating new Retrofit instance. Current token: " + (authToken != null ? authToken : "null"));
            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

// HEAD
            // Add authentication interceptor
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                // Lấy token từ SharedPreferences hoặc biến authToken
                String token = authToken;
                if ((token == null || token.isEmpty()) && context != null) {
                    SharedPreferences prefs = context.getSharedPreferences("TLUOfficeHours", Context.MODE_PRIVATE);
                    token = prefs.getString("auth_token", null);
                }
                if (token != null && !token.isEmpty()) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
                builder.addHeader("Accept", "application/json");
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
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}