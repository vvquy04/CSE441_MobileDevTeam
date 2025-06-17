package com.example.tluofficehours.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.repository.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // The registerUser method is removed as registration is now a two-step process handled by another ViewModel
}