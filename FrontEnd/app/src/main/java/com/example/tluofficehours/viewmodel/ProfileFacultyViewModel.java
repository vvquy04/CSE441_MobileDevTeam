package com.example.tluofficehours.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.User;
import com.example.tluofficehours.repository.FacultyRepository;

public class ProfileFacultyViewModel extends AndroidViewModel {
    
    private FacultyRepository repository;
    private MutableLiveData<FacultyProfile> facultyProfile = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public ProfileFacultyViewModel(@NonNull Application application) {
        super(application);
        repository = new FacultyRepository(application.getApplicationContext());
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
        repository.getProfile(new FacultyRepository.FacultyApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getFacultyProfile() != null) {
                    facultyProfile.postValue(user.getFacultyProfile());
                    email.postValue(user.getEmail());
                } else {
                    errorMessage.postValue("Không tìm thấy thông tin giảng viên");
                }
                isLoading.postValue(false);
            }
            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void updateFacultyProfile(FacultyProfile profile) {
        isLoading.setValue(true);
        // TODO: Update faculty profile via API
        // For now, just update local data
        facultyProfile.setValue(profile);
        successMessage.setValue("Cập nhật thông tin thành công");
        isLoading.setValue(false);
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void clearSuccessMessage() {
        successMessage.setValue(null);
    }
} 