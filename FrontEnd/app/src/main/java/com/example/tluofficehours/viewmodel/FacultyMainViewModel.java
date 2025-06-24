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
import com.example.tluofficehours.api.FacultyApiService;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class FacultyMainViewModel extends AndroidViewModel {
    private FacultyRepository repository;
    private MutableLiveData<FacultyApiService.DashboardResponse> dashboardData = new MutableLiveData<>();
    private MutableLiveData<List<Booking>> recentBookings = new MutableLiveData<>();
    private MutableLiveData<List<AvailableSlot>> upcomingSlots = new MutableLiveData<>();
    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Integer> todayBookingCount = new MutableLiveData<>(0);
    private MutableLiveData<Integer> todayAvailableSlotCount = new MutableLiveData<>(0);

    public FacultyMainViewModel(@NonNull Application application) {
        super(application);
        repository = new FacultyRepository(application.getApplicationContext());
    }

    // LiveData getters
    public LiveData<FacultyApiService.DashboardResponse> getDashboardData() { return dashboardData; }
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
        repository.getDashboardData(new FacultyRepository.FacultyApiCallback<FacultyApiService.DashboardResponse>() {
            @Override
            public void onSuccess(FacultyApiService.DashboardResponse result) {
                dashboardData.setValue(result);
                if (result.recentBookings != null) {
                    recentBookings.setValue(result.recentBookings);
                }
                upcomingSlots.setValue(result.upcomingSlots);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    // Load user profile
    public void loadUserProfile() {
        repository.getProfile(new FacultyRepository.FacultyApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                userProfile.setValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Approve booking
    public void approveBooking(int bookingId) {
        repository.approveBooking(bookingId, new FacultyRepository.FacultyApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking result) {
                // Reload dashboard data after approval
                loadDashboardData();
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Reject booking
    public void rejectBooking(int bookingId, String reason) {
        repository.rejectBooking(bookingId, reason, new FacultyRepository.FacultyApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking result) {
                // Reload dashboard data after rejection
                loadDashboardData();
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Cancel booking
    public void cancelBooking(int bookingId, String reason) {
        repository.cancelBooking(bookingId, reason, new FacultyRepository.FacultyApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking result) {
                // Reload dashboard data after cancellation
                loadDashboardData();
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Mark booking as completed
    public void markBookingCompleted(int bookingId) {
        repository.markBookingCompleted(bookingId, new FacultyRepository.FacultyApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking result) {
                // Reload dashboard data after completion
                loadDashboardData();
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Clear error message
    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    // Get formatted statistics
    public String getAvailableSlotsText() {
        FacultyApiService.DashboardResponse data = dashboardData.getValue();
        if (data != null) {
            return data.availableSlots + " Slot";
        }
        return "0 Slot";
    }

    public String getBookingsCountText() {
        FacultyApiService.DashboardResponse data = dashboardData.getValue();
        if (data != null) {
            return data.pendingBookings + " Sinh viên đã đặt lịch";
        }
        return "0 Sinh viên đã đặt lịch";
    }

    public String getUserDisplayName() {
        User user = userProfile.getValue();
        if (user != null && user.getFacultyProfile() != null) {
            return user.getFacultyProfile().getFacultyName();
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
        repository.getBookingsByWeek(
            sdf.format(start), sdf.format(end), null,
            new FacultyRepository.FacultyApiCallback<FacultyApiService.BookingsByWeekResponse>() {
                @Override
                public void onSuccess(FacultyApiService.BookingsByWeekResponse result) {
                    recentBookings.setValue(result.allBookings);
                }
                @Override
                public void onError(String message) {
                    errorMessage.setValue(message);
                }
            }
        );
    }

    public void loadTodayBookings() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new Date());
        repository.getBookingsByDate(today, null, new FacultyRepository.FacultyApiCallback<FacultyApiService.BookingsByDateResponse>() {
            @Override
            public void onSuccess(FacultyApiService.BookingsByDateResponse result) {
                Log.d("TODAY_BOOKINGS", new Gson().toJson(result.bookings));
                if (result != null && result.bookings != null) {
                    todayBookingCount.setValue(result.bookings.size());
                    int availableSlots = 0;
                    for (Booking booking : result.bookings) {
                        if (booking.getSlot() != null && booking.getSlot().getAvailableSpots() > 0) {
                            availableSlots++;
                        }
                    }
                    todayAvailableSlotCount.setValue(availableSlots);
                } else {
                    todayBookingCount.setValue(0);
                    todayAvailableSlotCount.setValue(0);
                }
            }
            @Override
            public void onError(String message) {
                todayBookingCount.setValue(0);
                todayAvailableSlotCount.setValue(0);
            }
        });
    }
} 