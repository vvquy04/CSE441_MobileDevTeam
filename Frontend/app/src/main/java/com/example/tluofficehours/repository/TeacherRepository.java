package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.Department;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unified Repository for all teacher-related operations
 * Consolidates functionality from:
 * - TeachListRepository
 * - FacultyDetailRepository
 * - StudentMainActivity (teacher-related methods)
 */
public class TeacherRepository {
    private ApiService apiService;

    public TeacherRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // ==================== TEACHER LISTING ====================
    
    /**
     * Lấy danh sách giảng viên nổi bật (featured teachers)
     */
    public void getFeaturedTeachers(TeachersCallback callback) {
        apiService.getFeaturedTeachers().enqueue(new Callback<List<FacultyProfile>>() {
            @Override
            public void onResponse(Call<List<FacultyProfile>> call, Response<List<FacultyProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get featured teachers: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FacultyProfile>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách giảng viên theo bộ môn
     */
    public void getTeachersByDepartment(String departmentId, TeachersCallback callback) {
        apiService.getTeachersByDepartment(departmentId).enqueue(new Callback<List<FacultyProfile>>() {
            @Override
            public void onResponse(Call<List<FacultyProfile>> call, Response<List<FacultyProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get teachers by department: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FacultyProfile>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Tìm kiếm giảng viên theo tên
     */
    public void searchTeachers(String query, TeachersCallback callback) {
        apiService.searchTeachers(query).enqueue(new Callback<List<FacultyProfile>>() {
            @Override
            public void onResponse(Call<List<FacultyProfile>> call, Response<List<FacultyProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to search teachers: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FacultyProfile>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // ==================== TEACHER DETAILS ====================
    
    /**
     * Lấy thông tin chi tiết giảng viên
     */
    public void getTeacherDetail(String facultyUserId, TeacherDetailCallback callback) {
        apiService.getTeacherDetail(facultyUserId).enqueue(new Callback<FacultyProfile>() {
            @Override
            public void onResponse(Call<FacultyProfile> call, Response<FacultyProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get teacher detail: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FacultyProfile> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // ==================== TEACHER AVAILABILITY ====================
    
    /**
     * Lấy danh sách slot trống của giảng viên theo ngày
     */
    public void getAvailableSlots(String facultyUserId, String date, Callback<List<AvailableSlot>> callback) {
        Call<List<AvailableSlot>> call = apiService.getAvailableSlots(facultyUserId, date);
        call.enqueue(callback);
    }

    // ==================== DEPARTMENTS ====================
    
    /**
     * Lấy danh sách các bộ môn
     */
    public void getDepartments(Callback<List<Department>> callback) {
        Call<List<Department>> call = apiService.getDepartments();
        call.enqueue(callback);
    }

    public interface TeachersCallback {
        void onSuccess(List<FacultyProfile> teachers);
        void onError(String message);
    }

    public interface TeacherDetailCallback {
        void onSuccess(FacultyProfile teacher);
        void onError(String message);
    }
} 