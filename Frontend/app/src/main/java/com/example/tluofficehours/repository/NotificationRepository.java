package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.Notification;

import java.util.List;

import retrofit2.Call;

public class NotificationRepository {
    private ApiService apiService;

    public NotificationRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public Call<List<Notification>> getNotifications() {
        return com.example.tluofficehours.api.RetrofitClient.getApiService().getNotifications();
    }
} 