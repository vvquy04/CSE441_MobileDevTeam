package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.Appointment;
import com.example.tluofficehours.model.Booking;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private ApiService apiService;

    public BookingRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void getStudentAppointments(Callback<List<Appointment>> callback) {
        apiService.getStudentAppointments().enqueue(callback);
    }

    public void bookAppointment(Map<String, Object> bookingData, Callback<ResponseBody> callback) {
        apiService.bookAppointment(bookingData).enqueue(callback);
    }

    public void cancelAppointment(int appointmentId, Map<String, String> body, Callback<ResponseBody> callback) {
        apiService.cancelAppointment(appointmentId, body).enqueue(callback);
    }
} 