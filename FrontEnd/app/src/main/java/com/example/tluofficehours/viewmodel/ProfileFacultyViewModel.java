package com.example.tluofficehours.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;

import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.User;
import com.example.tluofficehours.model.UserProfile;
import com.example.tluofficehours.repository.FacultyRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFacultyViewModel extends AndroidViewModel {
    
    private FacultyRepository repository;
    private MutableLiveData<FacultyProfile> facultyProfile = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public ProfileFacultyViewModel(@NonNull Application application) {
        super(application);
        repository = new FacultyRepository();
    }

    public LiveData<FacultyProfile> getFacultyProfile() {
        return facultyProfile;
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

    public LiveData<String> getEmail() {
        return email;
    }

    public void loadFacultyProfile() {
        isLoading.setValue(true);
        repository.getFacultyProfile().observeForever(profile -> {
            isLoading.setValue(false);
            if (profile != null) {
                facultyProfile.setValue(profile);
            } else {
                errorMessage.setValue("Không thể tải thông tin giảng viên");
            }
        });
    }

    public void updateFacultyProfile(Context context, String facultyName, String departmentId, String degree, String phoneNumber, String officeLocation, Uri avatarUri) {
        isLoading.setValue(true);
        repository.updateFacultyProfile(facultyName, departmentId, degree, phoneNumber, officeLocation, avatarUri, context)
            .observeForever(success -> {
                isLoading.setValue(false);
                if (success != null && success) {
                    successMessage.setValue("Cập nhật thông tin thành công!");
                    loadFacultyProfile();
                } else {
                    errorMessage.setValue("Cập nhật thất bại!");
                }
            });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void clearSuccessMessage() {
        successMessage.setValue(null);
    }
} 