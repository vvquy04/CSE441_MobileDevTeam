package com.example.tluofficehours.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.AddScheduleData;
import com.example.tluofficehours.repository.FacultyRepository;
import com.example.tluofficehours.view.AddScheduleActivity;

import org.json.JSONObject;

public class AddScheduleViewModel extends AndroidViewModel {
    private static final String TAG = "AddScheduleViewModel";
    private final FacultyRepository facultyRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AddScheduleViewModel(@NonNull Application application) {
        super(application);
        facultyRepository = new FacultyRepository(application);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void createSchedule(AddScheduleData scheduleData) {
        isLoading.setValue(true);
        Log.d(TAG, "Attempting to create schedule with data: " + scheduleData.toString());

        // Chọn API dựa trên chế độ tạo lịch
        if (scheduleData.isApplyMonthly() && !scheduleData.isSpecificDateMode()) {
            // Sử dụng API tạo lịch cho cả tháng (chỉ cho chế độ lịch cố định)
            facultyRepository.createMonthlySchedule(scheduleData, new FacultyRepository.FacultyApiCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    isLoading.setValue(false);
                    try {
                        String message = result.has("message") ? result.getString("message") : "Schedule created successfully";
                        successMessage.setValue(message);
                        Log.d(TAG, "Monthly schedule creation successful: " + message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing success response", e);
                        successMessage.setValue("Schedule created, but response was unreadable.");
                    }
                }

                @Override
                public void onError(String message) {
                    isLoading.setValue(false);
                    errorMessage.setValue(message);
                    Log.e(TAG, "Monthly schedule creation failed: " + message);
                }
            });
        } else {
            // Sử dụng API tạo lịch thông thường
            facultyRepository.createMultipleSlots(scheduleData, new FacultyRepository.FacultyApiCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    isLoading.setValue(false);
                    try {
                        String message = result.has("message") ? result.getString("message") : "Schedule created successfully";
                        successMessage.setValue(message);
                        Log.d(TAG, "Schedule creation successful: " + message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing success response", e);
                        successMessage.setValue("Schedule created, but response was unreadable.");
                    }
                }

                @Override
                public void onError(String message) {
                    isLoading.setValue(false);
                    errorMessage.setValue(message);
                    Log.e(TAG, "Schedule creation failed: " + message);
                }
            });
        }
    }

    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
} 