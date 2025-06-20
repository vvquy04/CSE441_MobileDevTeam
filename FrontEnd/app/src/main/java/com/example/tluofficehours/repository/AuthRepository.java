package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.Department;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public Call<ResponseBody> registerFaculty(RequestBody email, RequestBody password, RequestBody passwordConfirmation, RequestBody facultyName, RequestBody departmentId, RequestBody degree, RequestBody phoneNumber, RequestBody officeRoom, MultipartBody.Part avatar) {
        return apiService.registerFaculty(email, password, passwordConfirmation, facultyName, departmentId, degree, phoneNumber, officeRoom, avatar);
    }

    public Call<List<Department>> getDepartments() {
        return apiService.getDepartments();
    }

    public Call<ResponseBody> registerStudent(RequestBody email, RequestBody password, RequestBody passwordConfirmation, RequestBody studentName, RequestBody studentCode, RequestBody className, RequestBody phoneNumber, MultipartBody.Part avatar) {
        return apiService.registerStudent(email, password, passwordConfirmation, studentName, studentCode, className, phoneNumber, avatar);
    }

    public Call<ResponseBody> login(LoginRequest request) {
        return apiService.login(request);
    }
}
