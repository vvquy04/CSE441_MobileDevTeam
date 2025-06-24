package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.Appointment;
import com.example.tluofficehours.model.AvailableSlot;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Unified Repository for all appointment-related operations
 * Consolidates functionality from:
 * - BookAppointmentRepository
 * - MyAppointmentRepository
 * - AppointmentDetailRepository
 */
public class AppointmentRepository {
    private ApiService apiService;

    public AppointmentRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // ==================== STUDENT APPOINTMENTS ====================
    
    /**
     * Lấy danh sách lịch hẹn của sinh viên
     */
    public void getStudentAppointments(Callback<List<Appointment>> callback) {
        Call<List<Appointment>> call = apiService.getStudentAppointments();
        call.enqueue(callback);
    }

    // ==================== BOOKING APPOINTMENTS ====================
    
    /**
     * Lấy danh sách slot trống của giảng viên theo ngày
     */
    public void getAvailableSlots(String facultyUserId, String date, Callback<List<AvailableSlot>> callback) {
        Call<List<AvailableSlot>> call = apiService.getAvailableSlots(facultyUserId, date);
        call.enqueue(callback);
    }

    /**
     * Đặt lịch hẹn
     */
    public void bookAppointment(Map<String, Object> bookingData, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.bookAppointment(bookingData);
        call.enqueue(callback);
    }

    // ==================== APPOINTMENT DETAILS ====================
    
    /**
     * Hủy lịch hẹn
     */
    public void cancelAppointment(int appointmentId, String reason, Callback<ResponseBody> callback) {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("reason", reason);
        
        Call<ResponseBody> call = apiService.cancelAppointment(appointmentId, body);
        call.enqueue(callback);
    }
} 