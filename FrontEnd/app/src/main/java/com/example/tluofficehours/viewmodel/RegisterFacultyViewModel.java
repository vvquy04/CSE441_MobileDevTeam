package com.example.tluofficehours.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.RegisterFacultyRequest;
import com.example.tluofficehours.repository.AuthRepository;
import com.example.tluofficehours.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public void registerFaculty(Context context, String email, String password, String facultyName, String departmentId, String degree, String phoneNumber, String officeRoom, Uri avatarUri) {
        String passwordConfirmation = password;
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody passwordConfirmationBody = RequestBody.create(MediaType.parse("text/plain"), passwordConfirmation);
        RequestBody facultyNameBody = RequestBody.create(MediaType.parse("text/plain"), facultyName);
        RequestBody departmentIdBody = RequestBody.create(MediaType.parse("text/plain"), departmentId);
        RequestBody degreeBody = RequestBody.create(MediaType.parse("text/plain"), degree != null ? degree : "");
        RequestBody phoneNumberBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber != null ? phoneNumber : "");
        RequestBody officeRoomBody = RequestBody.create(MediaType.parse("text/plain"), officeRoom != null ? officeRoom : "");
        MultipartBody.Part avatarPart = null;
        if (avatarUri != null) {
            try {
                File file = FileUtils.createTempFileFromUri(context, avatarUri);
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), reqFile);
            } catch (IOException e) {
                errorMessage.setValue("Không thể đọc file ảnh: " + e.getMessage());
                return;
            }
        }
        authRepository.registerFaculty(emailBody, passwordBody, passwordConfirmationBody, facultyNameBody, departmentIdBody, degreeBody, phoneNumberBody, officeRoomBody, avatarPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Đăng ký giảng viên thành công!");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        StringBuilder errorMsg = new StringBuilder();
                        errorMsg.append("Mã lỗi: ").append(response.code()).append("\n");
                        switch (response.code()) {
                            case 400:
                                if (errorBody.contains("Email")) {
                                    errorMsg.append("Email không hợp lệ hoặc đã được sử dụng");
                                } else if (errorBody.contains("Password")) {
                                    errorMsg.append("Mật khẩu không đúng định dạng (cần ít nhất 6 ký tự)");
                                } else if (errorBody.contains("Department")) {
                                    errorMsg.append("Bộ môn không tồn tại trong hệ thống");
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
                                errorMsg.append("Email đã được sử dụng trong hệ thống");
                                break;
                            case 422:
                                if (errorBody.contains("department_id")) {
                                    errorMsg.append("Mã bộ môn không hợp lệ hoặc không tồn tại");
                                } else if (errorBody.contains("email")) {
                                    errorMsg.append("Email không hợp lệ hoặc đã được sử dụng");
                                } else if (errorBody.contains("password")) {
                                    errorMsg.append("Mật khẩu không đúng định dạng");
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
                errorMsg.append("\n\nThông tin request:");
                errorMsg.append("\nURL: ").append(call.request().url());
                errorMsg.append("\nPhương thức: ").append(call.request().method());
                errorMessage.setValue(errorMsg.toString());
            }
        });
    }
}