package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.Department;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public Call<ResponseBody> registerFaculty(RegisterFacultyRequest request) {
        return apiService.registerFaculty(request);
    }

    public Call<List<Department>> getDepartments() {
        return apiService.getDepartments();
    }

    public Call<ResponseBody> registerStudent(RegisterStudentRequest request) {
        return apiService.registerStudent(request);
    }

    public Call<ResponseBody> login(LoginRequest request) {
        return apiService.login(request);
    }
}
