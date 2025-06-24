package com.example.tluofficehours.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tluofficehours.model.Notification;
import com.example.tluofficehours.repository.NotificationRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    private NotificationRepository repository = new NotificationRepository();

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void loadNotifications() {
        repository.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notifications.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                notifications.setValue(null);
            }
        });
    }
} 