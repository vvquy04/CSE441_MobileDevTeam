package com.example.tluofficehours.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.repository.TeacherRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unified ViewModel for all teacher-related operations
 * Consolidates functionality from:
 * - TeachListViewModel
 * - FacultyDetailViewModel
 * - StudentMainActivity (teacher-related methods)
 */
public class TeacherViewModel extends ViewModel {
    private TeacherRepository teacherRepository;
    
    // LiveData for different teacher operations
    private MutableLiveData<List<FacultyProfile>> teachers = new MutableLiveData<>();
    private MutableLiveData<List<FacultyProfile>> featuredTeachers = new MutableLiveData<>();
    private MutableLiveData<FacultyProfile> teacherDetail = new MutableLiveData<>();
    private MutableLiveData<List<AvailableSlot>> availableSlots = new MutableLiveData<>();
    private MutableLiveData<List<Department>> departments = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public TeacherViewModel() {
        teacherRepository = new TeacherRepository();
    }

    // ==================== TEACHER LISTING ====================
    
    /**
     * Load featured teachers (for StudentMain screen)
     */
    public void loadFeaturedTeachers() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.getFeaturedTeachers(new TeacherRepository.TeachersCallback() {
            @Override
            public void onSuccess(List<FacultyProfile> teachers) {
                isLoading.setValue(false);
                featuredTeachers.setValue(teachers);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    /**
     * Load teachers by department (for TeachList screen)
     */
    public void loadTeachersByDepartment(String departmentId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.getTeachersByDepartment(departmentId, new TeacherRepository.TeachersCallback() {
            @Override
            public void onSuccess(List<FacultyProfile> teachers) {
                isLoading.setValue(false);
                TeacherViewModel.this.teachers.setValue(teachers);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    /**
     * Search teachers by name (for StudentMain screen)
     */
    public void searchTeachers(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadFeaturedTeachers();
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.searchTeachers(query, new TeacherRepository.TeachersCallback() {
            @Override
            public void onSuccess(List<FacultyProfile> teachers) {
                isLoading.setValue(false);
                TeacherViewModel.this.teachers.setValue(teachers);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // ==================== TEACHER DETAILS ====================
    
    /**
     * Load teacher detail (for FacultyDetail screen)
     */
    public void loadTeacherDetail(String facultyUserId) {
        android.util.Log.d("TeacherViewModel", "loadTeacherDetail called with facultyUserId: " + facultyUserId);
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.getTeacherDetail(facultyUserId, new TeacherRepository.TeacherDetailCallback() {
            @Override
            public void onSuccess(FacultyProfile teacher) {
                android.util.Log.d("TeacherViewModel", "Teacher detail loaded successfully: " + teacher.getFacultyName());
                isLoading.setValue(false);
                teacherDetail.setValue(teacher);
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("TeacherViewModel", "Error loading teacher detail: " + message);
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // ==================== TEACHER AVAILABILITY ====================
    
    /**
     * Load available slots for teacher (for FacultyDetail screen)
     */
    public void loadAvailableSlots(String facultyUserId, String date) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.getAvailableSlots(facultyUserId, date, new Callback<List<AvailableSlot>>() {
            @Override
            public void onResponse(Call<List<AvailableSlot>> call, Response<List<AvailableSlot>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    availableSlots.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách slot trống");
                }
            }

            @Override
            public void onFailure(Call<List<AvailableSlot>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== DEPARTMENTS ====================
    
    /**
     * Load departments (for StudentMain screen)
     */
    public void loadDepartments() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        teacherRepository.getDepartments(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    departments.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách bộ môn");
                }
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Get department name by ID - public method for adapter to call
     */
    public String getDepartmentNameById(String departmentId) {
        List<Department> deptList = departments.getValue();
        android.util.Log.d("TeacherViewModel", "getDepartmentNameById called with departmentId: " + departmentId);
        android.util.Log.d("TeacherViewModel", "departments list size: " + (deptList != null ? deptList.size() : "null"));
        
        if (deptList != null && departmentId != null) {
            for (Department dept : deptList) {
                android.util.Log.d("TeacherViewModel", "Checking dept: " + dept.getDepartmentId() + " vs " + departmentId);
                if (dept.getDepartmentId().equals(departmentId)) {
                    String name = dept.getName();
                    android.util.Log.d("TeacherViewModel", "Found department name: " + name);
                    return name;
                }
            }
        }
        android.util.Log.w("TeacherViewModel", "Department not found for ID: " + departmentId);
        return null;
    }

    // ==================== LIVE DATA GETTERS ====================
    
    public LiveData<List<FacultyProfile>> getTeachers() {
        return teachers;
    }

    public LiveData<List<FacultyProfile>> getFeaturedTeachers() {
        return featuredTeachers;
    }

    public LiveData<FacultyProfile> getTeacherDetail() {
        return teacherDetail;
    }

    public LiveData<List<AvailableSlot>> getAvailableSlots() {
        return availableSlots;
    }

    public LiveData<List<Department>> getDepartments() {
        return departments;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
} 