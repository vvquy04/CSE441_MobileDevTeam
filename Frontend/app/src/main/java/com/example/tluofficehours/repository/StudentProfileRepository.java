package com.example.tluofficehours.repository;

import android.util.Log;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.model.Teacher;
import com.example.tluofficehours.utils.FileUtils;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentProfileRepository {
    private static final String TAG = "StudentProfileRepository";
    private ApiService apiService;

    public StudentProfileRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void getStudentProfile(StudentProfileCallback callback) {
        com.example.tluofficehours.api.RetrofitClient.getApiService().getStudentProfile().enqueue(new Callback<StudentProfile>() {
            @Override
            public void onResponse(Call<StudentProfile> call, Response<StudentProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "getStudentProfile success: " + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "getStudentProfile error: " + response.code() + " - " + response.message());
                    callback.onError("Không thể lấy thông tin profile sinh viên");
                }
            }

            @Override
            public void onFailure(Call<StudentProfile> call, Throwable t) {
                Log.e(TAG, "getStudentProfile failure: " + t.getMessage());
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Commented out multipart method since JSON method is working
    /*
    public void updateStudentProfile(String studentName, String phoneNumber, String className, String imagePath, UpdateStudentProfileCallback callback) {
        Log.d(TAG, "updateStudentProfile called with: name=" + studentName + ", phone=" + phoneNumber + ", class=" + className);
        
        RequestBody nameBody = RequestBody.create(MultipartBody.FORM, studentName);
        RequestBody phoneBody = RequestBody.create(MultipartBody.FORM, phoneNumber);
        RequestBody classBody = RequestBody.create(MultipartBody.FORM, className);
        
        MultipartBody.Part avatarPart = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            avatarPart = FileUtils.createImagePart("avatar", imagePath);
        }
        
        Call<ResponseBody> call = apiService.updateStudentProfile(nameBody, phoneBody, classBody, avatarPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "updateStudentProfile response: " + response.code() + " - " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "updateStudentProfile success");
                    callback.onSuccess("Cập nhật thông tin thành công");
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Unknown error";
                    }
                    Log.e(TAG, "updateStudentProfile error: " + response.code() + " - " + errorBody);
                    callback.onError("Không thể cập nhật thông tin: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "updateStudentProfile failure: " + t.getMessage());
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    */

    public void updateStudentProfileJson(String studentName, String phoneNumber, String className, UpdateStudentProfileCallback callback) {
        Log.d(TAG, "updateStudentProfileJson called with: name=" + studentName + ", phone=" + phoneNumber + ", class=" + className);
        
        Map<String, String> profileData = new HashMap<>();
        profileData.put("StudentName", studentName);
        profileData.put("PhoneNumber", phoneNumber);
        profileData.put("ClassName", className);
        
        Call<ResponseBody> call = apiService.updateStudentProfileJson(profileData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "updateStudentProfileJson response: " + response.code() + " - " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "updateStudentProfileJson success");
                    callback.onSuccess("Cập nhật thông tin thành công");
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Unknown error";
                    }
                    Log.e(TAG, "updateStudentProfileJson error: " + response.code() + " - " + errorBody);
                    callback.onError("Không thể cập nhật thông tin: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "updateStudentProfileJson failure: " + t.getMessage());
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void searchTeachers(String query, Callback<List<Teacher>> callback) {
        com.example.tluofficehours.api.RetrofitClient.getApiService().searchTeachers(query).enqueue(callback);
    }

    public void getTeacherDetail(String facultyUserId, Callback<Teacher> callback) {
        com.example.tluofficehours.api.RetrofitClient.getApiService().getTeacherDetail(facultyUserId).enqueue(callback);
    }

    public interface StudentProfileCallback {
        void onSuccess(StudentProfile studentProfile);
        void onError(String message);
    }

    public interface UpdateStudentProfileCallback {
        void onSuccess(String message);
        void onError(String message);
    }
} 