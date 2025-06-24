package com.example.tluofficehours.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.User;
import com.example.tluofficehours.repository.FacultyRepository;
import com.example.tluofficehours.api.ApiService;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class FacultyMainViewModel extends AndroidViewModel {
    private FacultyRepository repository;
    private MutableLiveData<ApiService.DashboardResponse> dashboardData = new MutableLiveData<>();
    private MutableLiveData<List<Booking>> recentBookings = new MutableLiveData<>();
    private MutableLiveData<List<AvailableSlot>> upcomingSlots = new MutableLiveData<>();
    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Integer> todayBookingCount = new MutableLiveData<>(0);
    private MutableLiveData<Integer> todayAvailableSlotCount = new MutableLiveData<>(0);

    public FacultyMainViewModel(@NonNull Application application) {
        super(application);
        repository = new FacultyRepository();
    }

    // LiveData getters
    public LiveData<ApiService.DashboardResponse> getDashboardData() { return dashboardData; }
    public LiveData<List<Booking>> getRecentBookings() { return recentBookings; }
    public LiveData<List<AvailableSlot>> getUpcomingSlots() { return upcomingSlots; }
    public LiveData<User> getUserProfile() { return userProfile; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Integer> getTodayBookingCount() { return todayBookingCount; }
    public LiveData<Integer> getTodayAvailableSlotCount() { return todayAvailableSlotCount; }

    // Load dashboard data
    public void loadDashboardData() {
        isLoading.setValue(true);
        repository.getDashboardData().observeForever(result -> {
            dashboardData.setValue(result);
            if (result != null && result.recentBookings != null) {
                recentBookings.setValue(result.recentBookings);
            }
            if (result != null) {
                upcomingSlots.setValue(result.upcomingSlots);
            }
            isLoading.setValue(false);
        });
    }

    // Load user profile
    public void loadUserProfile() {
        repository.getProfile().observeForever(result -> {
            userProfile.setValue(result);
        });
    }

    // Approve booking
    public void approveBooking(int bookingId) {
        repository.approveBooking(bookingId).observeForever(result -> {
            loadDashboardData();
        });
    }

    // Reject booking
    public void rejectBooking(int bookingId, String reason) {
        repository.rejectBooking(bookingId, reason).observeForever(result -> {
            loadDashboardData();
        });
    }

    // Cancel booking
    public void cancelBooking(int bookingId, String reason) {
        repository.cancelBooking(bookingId, reason).observeForever(result -> {
            loadDashboardData();
        });
    }

    // Mark booking as completed
    public void markBookingCompleted(int bookingId) {
        repository.markBookingCompleted(bookingId).observeForever(result -> {
            loadDashboardData();
        });
    }

    // Clear error message
    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    // Get formatted statistics
    public String getAvailableSlotsText() {
        ApiService.DashboardResponse data = dashboardData.getValue();
        if (data != null) {
            return data.availableSlots + " Slot";
        }
        return "0 Slot";
    }

    public String getBookingsCountText() {
        ApiService.DashboardResponse data = dashboardData.getValue();
        if (data != null) {
            return data.pendingBookings + " Sinh viên đã đặt lịch";
        }
        return "0 Sinh viên đã đặt lịch";
    }

    public String getUserDisplayName() {
        User user = userProfile.getValue();
        if (user != null) {
            return user.getEmail();
        }
        return "Thầy / Cô";
    }

    public void loadBookingsForCurrentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date start = cal.getTime();
        cal.add(Calendar.DATE, 6);
        Date end = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        repository.getBookingsByWeek(sdf.format(start), sdf.format(end), null).observeForever(result -> {
            if (result != null) {
                recentBookings.setValue(result);
            }
        });
    }

    public void loadTodayBookings() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new Date());
        repository.getBookingsByDate(today, null).observeForever(result -> {
            if (result != null) {
                todayBookingCount.setValue(result.size());
                todayAvailableSlotCount.setValue(0); // TODO: Tính số slot khả dụng nếu cần
            } else {
                todayBookingCount.setValue(0);
                todayAvailableSlotCount.setValue(0);
            }
        });
    }
} 