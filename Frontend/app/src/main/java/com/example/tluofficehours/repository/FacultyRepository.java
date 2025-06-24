package com.example.tluofficehours.repository;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.UserProfile;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import android.net.Uri;
import com.example.tluofficehours.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FacultyRepository {
    private ApiService apiService;

    public FacultyRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<FacultyProfile> getFacultyProfile() {
        MutableLiveData<FacultyProfile> data = new MutableLiveData<>();
        apiService.getFacultyProfile().enqueue(new Callback<FacultyProfile>() {
            @Override
            public void onResponse(Call<FacultyProfile> call, Response<FacultyProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<FacultyProfile> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<FacultyProfile> getFacultyDetail(String facultyUserId) {
        MutableLiveData<FacultyProfile> data = new MutableLiveData<>();
        apiService.getTeacherDetail(facultyUserId).enqueue(new Callback<FacultyProfile>() {
            @Override
            public void onResponse(Call<FacultyProfile> call, Response<FacultyProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<FacultyProfile> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Lấy danh sách booking theo tuần
    public LiveData<List<Booking>> getBookingsByWeek(String startDate, String endDate, String status) {
        MutableLiveData<List<Booking>> data = new MutableLiveData<>();
        apiService.getBookingsByWeek(startDate, endDate, status).enqueue(new retrofit2.Callback<ApiService.BookingsByWeekResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingsByWeekResponse> call, retrofit2.Response<ApiService.BookingsByWeekResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().allBookings != null) {
                    data.postValue(response.body().allBookings);
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingsByWeekResponse> call, Throwable t) {
                data.postValue(new ArrayList<>());
            }
        });
        return data;
    }

    // Lấy danh sách booking theo ngày
    public LiveData<List<Booking>> getBookingsByDate(String date, String status) {
        MutableLiveData<List<Booking>> data = new MutableLiveData<>();
        apiService.getBookingsByDate(date, status).enqueue(new retrofit2.Callback<ApiService.BookingsByDateResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingsByDateResponse> call, retrofit2.Response<ApiService.BookingsByDateResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().bookings != null) {
                    data.postValue(response.body().bookings);
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingsByDateResponse> call, Throwable t) {
                data.postValue(new ArrayList<>());
            }
        });
        return data;
    }

    // Lấy danh sách booking theo trạng thái
    public LiveData<List<Booking>> getBookingsByStatus(String status, Integer limit) {
        MutableLiveData<List<Booking>> data = new MutableLiveData<>();
        apiService.getBookingsByStatus(status, limit).enqueue(new retrofit2.Callback<ApiService.BookingsByStatusResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingsByStatusResponse> call, retrofit2.Response<ApiService.BookingsByStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().bookings != null) {
                    data.postValue(response.body().bookings);
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingsByStatusResponse> call, Throwable t) {
                data.postValue(new ArrayList<>());
            }
        });
        return data;
    }

    // Xác nhận lịch hẹn
    public LiveData<Booking> approveBooking(int bookingId) {
        MutableLiveData<Booking> data = new MutableLiveData<>();
        apiService.approveBooking(bookingId).enqueue(new retrofit2.Callback<ApiService.BookingActionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingActionResponse> call, retrofit2.Response<ApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().booking != null) {
                    data.postValue(response.body().booking);
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingActionResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Hủy lịch hẹn
    public LiveData<Booking> cancelBooking(int bookingId, String reason) {
        MutableLiveData<Booking> data = new MutableLiveData<>();
        apiService.cancelBooking(bookingId, new ApiService.CancelBookingRequest(reason)).enqueue(new retrofit2.Callback<ApiService.BookingActionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingActionResponse> call, retrofit2.Response<ApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().booking != null) {
                    data.postValue(response.body().booking);
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingActionResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Từ chối lịch hẹn
    public LiveData<Booking> rejectBooking(int bookingId, String reason) {
        MutableLiveData<Booking> data = new MutableLiveData<>();
        apiService.rejectBooking(bookingId, new ApiService.RejectBookingRequest(reason)).enqueue(new retrofit2.Callback<ApiService.BookingActionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingActionResponse> call, retrofit2.Response<ApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().booking != null) {
                    data.postValue(response.body().booking);
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingActionResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Đánh dấu hoàn thành lịch hẹn
    public LiveData<Booking> markBookingCompleted(int bookingId) {
        MutableLiveData<Booking> data = new MutableLiveData<>();
        apiService.markBookingCompleted(bookingId).enqueue(new retrofit2.Callback<ApiService.BookingActionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.BookingActionResponse> call, retrofit2.Response<ApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().booking != null) {
                    data.postValue(response.body().booking);
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiService.BookingActionResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Dashboard data
    public LiveData<ApiService.DashboardResponse> getDashboardData() {
        MutableLiveData<ApiService.DashboardResponse> data = new MutableLiveData<>();
        data.postValue(null);
        return data;
    }

    // Profile data
    public LiveData<User> getProfile() {
        MutableLiveData<User> data = new MutableLiveData<>();
        data.postValue(null);
        return data;
    }

    public LiveData<Boolean> updateFacultyProfile(String facultyName, String departmentId, String degree, String phoneNumber, String officeLocation, Uri avatarUri, Context context) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        RequestBody facultyNameBody = RequestBody.create(MediaType.parse("text/plain"), facultyName != null ? facultyName : "");
        RequestBody departmentIdBody = RequestBody.create(MediaType.parse("text/plain"), departmentId != null ? departmentId : "");
        RequestBody degreeBody = RequestBody.create(MediaType.parse("text/plain"), degree != null ? degree : "");
        RequestBody phoneNumberBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber != null ? phoneNumber : "");
        RequestBody officeLocationBody = RequestBody.create(MediaType.parse("text/plain"), officeLocation != null ? officeLocation : "");
        MultipartBody.Part avatarPart = null;
        if (avatarUri != null) {
            try {
                File file = FileUtils.createTempFileFromUri(context, avatarUri);
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), reqFile);
            } catch (IOException e) {
                result.postValue(false);
                return result;
            }
        }
        apiService.updateFacultyProfile(facultyNameBody, departmentIdBody, degreeBody, phoneNumberBody, officeLocationBody, avatarPart)
            .enqueue(new Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                    result.postValue(response.isSuccessful());
                }
                @Override
                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                    result.postValue(false);
                }
            });
        return result;
    }

    public LiveData<Boolean> createSchedule(Object scheduleData) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        boolean isMonthly = false;
        if (scheduleData instanceof java.util.Map) {
            Object val = ((java.util.Map<?, ?>) scheduleData).get("apply_monthly");
            if (val instanceof Boolean) isMonthly = (Boolean) val;
        }
        retrofit2.Call<okhttp3.ResponseBody> call;
        if (isMonthly) {
            call = apiService.createMonthlySchedule(scheduleData);
        } else {
            call = apiService.createMultipleSlots(scheduleData);
        }
        call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                result.postValue(response.isSuccessful());
            }
            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                result.postValue(false);
            }
        });
        return result;
    }
} 