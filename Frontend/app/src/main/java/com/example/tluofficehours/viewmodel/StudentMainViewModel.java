package com.example.tluofficehours.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.model.Teacher;
import com.example.tluofficehours.repository.StudentProfileRepository;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentMainViewModel extends ViewModel {
    private StudentProfileRepository studentProfileRepository;
    private ApiService apiService;
    
    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> avatarUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Teacher>> featuredTeachers = new MutableLiveData<>();

    public StudentMainViewModel() {
        studentProfileRepository = new StudentProfileRepository();
        apiService = RetrofitClient.getApiService();
    }

    public void loadUserProfile() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        studentProfileRepository.getStudentProfile(new StudentProfileRepository.StudentProfileCallback() {
            @Override
            public void onSuccess(StudentProfile studentProfile) {
                isLoading.setValue(false);
                
                if (studentProfile.getProfile() != null) {
                    String name = studentProfile.getProfile().getStudentName();
                    if (name != null && !name.isEmpty()) {
                        userName.setValue(name);
                        android.util.Log.d("StudentMainViewModel", "Set user name: " + name);
                    } else {
                        if (studentProfile.getUser() != null && studentProfile.getUser().getEmail() != null) {
                            String email = studentProfile.getUser().getEmail();
                            userName.setValue(email);
                            android.util.Log.w("StudentMainViewModel", "Using email as fallback: " + email);
                        } else {
                            android.util.Log.w("StudentMainViewModel", "No name or email available");
                        }
                    }
                } else {
                    android.util.Log.w("StudentMainViewModel", "Profile object is null");
                }
                
                if (studentProfile.getAvatarUrl() != null && !studentProfile.getAvatarUrl().isEmpty()) {
                    avatarUrl.setValue(studentProfile.getAvatarUrl());
                }
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
                android.util.Log.e("StudentMainViewModel", "Error loading profile: " + message);
            }
        });
    }

    public void loadFeaturedTeachers() {
        Call<List<Teacher>> call = apiService.getFeaturedTeachers();
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    featuredTeachers.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách giảng viên");
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Teacher>> getFeaturedTeachers() {
        return featuredTeachers;
    }
}
