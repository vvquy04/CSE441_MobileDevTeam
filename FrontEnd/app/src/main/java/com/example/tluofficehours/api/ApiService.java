package com.example.tluofficehours.api;

import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.Department;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @POST("api/auth/register-faculty")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    Call<ResponseBody> registerFaculty(@Body RegisterFacultyRequest request);

    @GET("api/departments")
    @Headers({
        "Accept: application/json"
    })
    Call<List<Department>> getDepartments();

    @POST("api/auth/register-student")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    Call<ResponseBody> registerStudent(@Body RegisterStudentRequest request);

    @POST("api/login")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    Call<ResponseBody> login(@Body LoginRequest request);
}
