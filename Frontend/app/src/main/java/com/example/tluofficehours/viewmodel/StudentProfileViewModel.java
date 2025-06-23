package com.example.tluofficehours.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.repository.StudentProfileRepository;
import com.example.tluofficehours.utils.SharedPrefsManager;

public class StudentProfileViewModel extends ViewModel {
    private StudentProfileRepository studentProfileRepository;
    private SharedPrefsManager sharedPrefsManager;
    
    private MutableLiveData<StudentProfile> studentProfile = new MutableLiveData<>();
    private MutableLiveData<String> avatarUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>(false);

    public StudentProfileViewModel() {
        studentProfileRepository = new StudentProfileRepository();
    }

    public void setSharedPrefsManager(SharedPrefsManager sharedPrefsManager) {
        this.sharedPrefsManager = sharedPrefsManager;
    }

    public void loadStudentProfile() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        studentProfileRepository.getStudentProfile(new StudentProfileRepository.StudentProfileCallback() {
            @Override
            public void onSuccess(StudentProfile profile) {
                isLoading.setValue(false);
                studentProfile.setValue(profile);
                
                if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                    avatarUrl.setValue(profile.getAvatarUrl());
                }
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // Commented out multipart method since JSON method is working
    /*
    public void updateStudentProfile(String name, String phone, String className, String imagePath) {
        android.util.Log.d("StudentProfileViewModel", "updateStudentProfile called with: name=" + name + 
            ", phone=" + phone + ", class=" + className + ", imagePath=" + imagePath);
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        updateSuccess.setValue(false);

        studentProfileRepository.updateStudentProfile(name, phone, className, imagePath, new StudentProfileRepository.UpdateStudentProfileCallback() {
            @Override
            public void onSuccess(String message) {
                android.util.Log.d("StudentProfileViewModel", "Update successful: " + message);
                isLoading.setValue(false);
                updateSuccess.setValue(true);
                // Reload profile data immediately
                loadStudentProfile();
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("StudentProfileViewModel", "Update failed: " + message);
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }
    */
    
    public void updateStudentProfile(String name, String phone, String className) {
        android.util.Log.d("StudentProfileViewModel", "updateStudentProfile called with: name=" + name + 
            ", phone=" + phone + ", class=" + className);
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        updateSuccess.setValue(false);

        studentProfileRepository.updateStudentProfileJson(name, phone, className, new StudentProfileRepository.UpdateStudentProfileCallback() {
            @Override
            public void onSuccess(String message) {
                android.util.Log.d("StudentProfileViewModel", "Update successful: " + message);
                isLoading.setValue(false);
                updateSuccess.setValue(true);
                // Reload profile data immediately
                loadStudentProfile();
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("StudentProfileViewModel", "Update failed: " + message);
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }
    
    public void resetUpdateSuccess() {
        updateSuccess.setValue(false);
    }

    public LiveData<StudentProfile> getStudentProfile() {
        return studentProfile;
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

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }
} 