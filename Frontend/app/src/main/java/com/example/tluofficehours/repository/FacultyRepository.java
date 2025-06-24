package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacultyRepository {
    private ApiService apiService;

    public FacultyRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void getFacultyProfile(Callback<UserProfile> callback) {
        apiService.getFacultyProfile().enqueue(callback);
    }

    public void getFacultyDetail(String facultyUserId, Callback<FacultyProfile> callback) {
        apiService.getTeacherDetail(facultyUserId).enqueue(callback);
    }
} 