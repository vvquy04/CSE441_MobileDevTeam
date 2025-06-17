package com.example.tluofficehours.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.repository.AuthRepository;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFacultyViewModel extends AndroidViewModel {
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private AuthRepository authRepository;

    public RegisterFacultyViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerFaculty(String email, String password, String facultyName, String departmentId, String biography, String phoneNumber, String officeRoom) {
        String passwordConfirmation = password; // Assuming password and password_confirmation are the same at this stage
        RegisterFacultyRequest request = new RegisterFacultyRequest(email, password, passwordConfirmation, facultyName, departmentId, biography, phoneNumber, officeRoom);

        authRepository.registerFaculty(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Đăng ký giảng viên thành công!");
                } else {
                    try {
                        errorMessage.setValue("Lỗi: " + response.errorBody().string());
                    } catch (IOException e) {
                        errorMessage.setValue("Không thể xử lý phản hồi lỗi từ máy chủ.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                errorMessage.setValue("Lỗi mạng: " + t.getMessage());
            }
        });
    }
} 