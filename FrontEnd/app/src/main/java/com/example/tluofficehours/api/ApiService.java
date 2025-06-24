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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.List;

public interface ApiService {
    
    // ==================== AUTHENTICATION APIs ====================
    
    @POST("api/login")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ResponseBody> login(@Body LoginRequest request);

    @Multipart
    @POST("api/auth/register-faculty")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> registerFaculty(
        @Part("email") RequestBody email,
        @Part("password") RequestBody password,
        @Part("password_confirmation") RequestBody passwordConfirmation,
        @Part("faculty_name") RequestBody facultyName,
        @Part("department_id") RequestBody departmentId,
        @Part("degree") RequestBody degree,
        @Part("phone_number") RequestBody phoneNumber,
        @Part("office_room") RequestBody officeRoom,
        @Part MultipartBody.Part avatar
    );

    @Multipart
    @POST("api/auth/register-student")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> registerStudent(
        @Part("email") RequestBody email,
        @Part("password") RequestBody password,
        @Part("password_confirmation") RequestBody passwordConfirmation,
        @Part("StudentName") RequestBody studentName,
        @Part("StudentCode") RequestBody studentCode,
        @Part("ClassName") RequestBody className,
        @Part("PhoneNumber") RequestBody phoneNumber,
        @Part MultipartBody.Part avatar
    );

    // ==================== COMMON APIs ====================
    
    @GET("api/departments")
    @Headers({"Accept: application/json"})
    Call<List<Department>> getDepartments();
}
