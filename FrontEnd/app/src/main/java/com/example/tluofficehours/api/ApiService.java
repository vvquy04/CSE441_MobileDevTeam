package com.example.tluofficehours.api;

import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.LoginRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register-faculty")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> registerFaculty(@Body RegisterFacultyRequest request);

    @POST("register-student")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> registerStudent(@Body RegisterStudentRequest request);

    @POST("login")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> login(@Body LoginRequest request);

    // You might also need a login API method here if not already defined
    // @POST("login")
    // @Headers("Content-Type: application/json")
    // Call<ResponseBody> login(@Body LoginRequest request);
}
