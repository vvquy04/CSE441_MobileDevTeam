package com.example.tluofficehours.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.repository.AuthRepository;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterStudentViewModel extends AndroidViewModel {
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private AuthRepository authRepository;

    public RegisterStudentViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public void registerStudent(String email, String password, String studentName, String studentCode, String className, String phoneNumber) {
        String passwordConfirmation = password;
        RegisterStudentRequest request = new RegisterStudentRequest(email, password, passwordConfirmation, studentName, studentCode, className, phoneNumber);

        authRepository.registerStudent(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Đăng ký sinh viên thành công!");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        StringBuilder errorMsg = new StringBuilder();

                        // Thêm mã lỗi HTTP vào thông báo
                        errorMsg.append("Mã lỗi: ").append(response.code()).append("\n");

                        // Xử lý các trường hợp lỗi cụ thể
                        switch (response.code()) {
                            case 400:
                                if (errorBody.contains("Email")) {
                                    errorMsg.append("Email không hợp lệ hoặc đã được sử dụng");
                                } else if (errorBody.contains("Password")) {
                                    errorMsg.append("Mật khẩu không đúng định dạng (cần ít nhất 6 ký tự)");
                                } else {
                                    errorMsg.append("Dữ liệu không hợp lệ: ").append(errorBody.replaceAll("<[^>]*>", "").trim());
                                }
                                break;
                            case 401:
                                errorMsg.append("Không có quyền truy cập. Vui lòng đăng nhập lại");
                                break;
                            case 403:
                                errorMsg.append("Bạn không có quyền thực hiện hành động này");
                                break;
                            case 404:
                                errorMsg.append("Không tìm thấy tài nguyên yêu cầu");
                                break;
                            case 409:
                                errorMsg.append("Email hoặc mã sinh viên đã được sử dụng trong hệ thống");
                                break;
                            case 422:
                                if (errorBody.contains("email")) {
                                    errorMsg.append("Email không hợp lệ hoặc đã được sử dụng");
                                } else if (errorBody.contains("password")) {
                                    errorMsg.append("Mật khẩu không đúng định dạng");
                                } else if (errorBody.contains("StudentCode")) {
                                    errorMsg.append("Mã sinh viên đã được sử dụng");
                                } else {
                                    errorMsg.append("Dữ liệu không hợp lệ: ").append(errorBody.replaceAll("<[^>]*>", "").trim());
                                }
                                break;
                            case 500:
                                errorMsg.append("Lỗi máy chủ. Chi tiết: ").append(errorBody.replaceAll("<[^>]*>", "").trim());
                                break;
                            default:
                                errorMsg.append("Lỗi không xác định. Chi tiết: ").append(errorBody.replaceAll("<[^>]*>", "").trim());
                        }

                        // In ra URL và method của request gặp lỗi
                        errorMsg.append("\nURL: ").append(call.request().url());
                        errorMsg.append("\nPhương thức: ").append(call.request().method());

                        errorMessage.setValue(errorMsg.toString());
                    } catch (IOException e) {
                        errorMessage.setValue("Lỗi xử lý phản hồi từ máy chủ:\n" + e.getMessage() +
                                           "\nVui lòng kiểm tra kết nối mạng và thử lại.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Lỗi kết nối: ");

                if (t instanceof IOException) {
                    errorMsg.append("Không thể kết nối đến máy chủ\n");
                    errorMsg.append("Nguyên nhân: ").append(t.getMessage());
                    errorMsg.append("\nVui lòng kiểm tra:\n");
                    errorMsg.append("1. Kết nối internet\n");
                    errorMsg.append("2. Máy chủ có đang hoạt động\n");
                    errorMsg.append("3. Địa chỉ máy chủ đúng");
                } else {
                    errorMsg.append("Lỗi không xác định\n");
                    errorMsg.append("Chi tiết: ").append(t.getMessage());
                }

                // Thêm thông tin về request
                errorMsg.append("\n\nThông tin request:");
                errorMsg.append("\nURL: ").append(call.request().url());
                errorMsg.append("\nPhương thức: ").append(call.request().method());

                errorMessage.setValue(errorMsg.toString());
            }
        });
    }
} 