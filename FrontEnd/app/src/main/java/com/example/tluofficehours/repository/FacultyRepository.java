package com.example.tluofficehours.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.tluofficehours.api.FacultyApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.AddScheduleData;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.model.TimeSlot;
import com.example.tluofficehours.model.User;
import com.example.tluofficehours.view.AddScheduleActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class FacultyRepository {
    private static final String TAG = "FacultyRepository";
    private FacultyApiService apiService;
    private SharedPreferences sharedPreferences;
    
    public FacultyRepository(Context context) {
        try {
            // Initialize RetrofitClient with context
            RetrofitClient.init(context);
            // Create Retrofit instance and FacultyApiService
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            this.apiService = retrofit.create(FacultyApiService.class);
            this.sharedPreferences = context.getSharedPreferences("TLUOfficeHours", Context.MODE_PRIVATE);
            
            // Log để kiểm tra SharedPreferences
            String token = sharedPreferences.getString("auth_token", null);
            String email = sharedPreferences.getString("user_email", null);
            Log.d(TAG, "FacultyRepository initialized successfully");
            Log.d(TAG, "SharedPreferences - Token: " + (token != null ? "EXISTS" : "NULL"));
            Log.d(TAG, "SharedPreferences - Email: " + email);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing FacultyRepository: " + e.getMessage());
        }
    }
    
    // Dashboard
    public void getDashboardData(FacultyApiCallback<FacultyApiService.DashboardResponse> callback) {
        apiService.getFacultyDashboard().enqueue(new Callback<FacultyApiService.DashboardResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.DashboardResponse> call, 
                                 Response<FacultyApiService.DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải dữ liệu dashboard");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.DashboardResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Available Slots
    public void getAvailableSlots(FacultyApiCallback<List<AvailableSlot>> callback) {
        apiService.getFacultySlots().enqueue(new Callback<List<AvailableSlot>>() {
            @Override
            public void onResponse(Call<List<AvailableSlot>> call, Response<List<AvailableSlot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách slot");
                }
            }
            
            @Override
            public void onFailure(Call<List<AvailableSlot>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void createSlot(String startTime, String endTime, int maxStudents, 
                          Integer definitionId, FacultyApiCallback<AvailableSlot> callback) {
        FacultyApiService.CreateSlotRequest request = 
            new FacultyApiService.CreateSlotRequest(startTime, endTime, maxStudents, definitionId);
        
        apiService.createFacultySlot(request).enqueue(new Callback<FacultyApiService.SlotResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.SlotResponse> call, Response<FacultyApiService.SlotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().slot);
                } else {
                    callback.onError("Không thể tạo slot mới");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.SlotResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void updateSlot(int slotId, String startTime, String endTime, int maxStudents, 
                          boolean isAvailable, Integer definitionId, FacultyApiCallback<AvailableSlot> callback) {
        FacultyApiService.UpdateSlotRequest request = new FacultyApiService.UpdateSlotRequest();
        request.startTime = startTime;
        request.endTime = endTime;
        request.maxStudents = maxStudents;
        request.isAvailable = isAvailable;
        request.definitionId = definitionId;
        
        apiService.updateFacultySlot(slotId, request).enqueue(new Callback<FacultyApiService.SlotResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.SlotResponse> call, Response<FacultyApiService.SlotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().slot);
                } else {
                    callback.onError("Không thể cập nhật slot");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.SlotResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void deleteSlot(int slotId, FacultyApiCallback<String> callback) {
        apiService.deleteFacultySlot(slotId).enqueue(new Callback<FacultyApiService.MessageResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.MessageResponse> call, Response<FacultyApiService.MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().message);
                } else {
                    callback.onError("Không thể xóa slot");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.MessageResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void toggleSlotAvailability(int slotId, FacultyApiCallback<AvailableSlot> callback) {
        apiService.toggleFacultySlotAvailability(slotId).enqueue(new Callback<FacultyApiService.SlotResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.SlotResponse> call, Response<FacultyApiService.SlotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().slot);
                } else {
                    callback.onError("Không thể thay đổi trạng thái slot");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.SlotResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Bookings
    public void getBookings(FacultyApiCallback<List<Booking>> callback) {
        apiService.getFacultyBookings().enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách lịch hẹn");
                }
            }
            
            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getPendingBookings(FacultyApiCallback<List<Booking>> callback) {
        apiService.getFacultyPendingBookings().enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải lịch hẹn chờ xử lý");
                }
            }
            
            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getConfirmedBookings(FacultyApiCallback<List<Booking>> callback) {
        apiService.getFacultyConfirmedBookings().enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải lịch hẹn đã xác nhận");
                }
            }
            
            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // New Booking APIs
    public void getBookingsByDate(String date, String status, FacultyApiCallback<FacultyApiService.BookingsByDateResponse> callback) {
        apiService.getBookingsByDate(date, status).enqueue(new Callback<FacultyApiService.BookingsByDateResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingsByDateResponse> call, Response<FacultyApiService.BookingsByDateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải lịch hẹn theo ngày");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingsByDateResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getBookingsByWeek(String startDate, String endDate, String status, FacultyApiCallback<FacultyApiService.BookingsByWeekResponse> callback) {
        Log.d(TAG, "getBookingsByWeek: Calling API with startDate=" + startDate + ", endDate=" + endDate + ", status=" + status);
        
        // Check if token exists
        String token = sharedPreferences.getString("auth_token", null);
        Log.d(TAG, "getBookingsByWeek: Token exists: " + (token != null ? "YES" : "NO"));
        if (token != null) {
            Log.d(TAG, "getBookingsByWeek: Token length: " + token.length());
            Log.d(TAG, "getBookingsByWeek: Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");
        }
        
        apiService.getBookingsByWeek(startDate, endDate, status).enqueue(new Callback<FacultyApiService.BookingsByWeekResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingsByWeekResponse> call, Response<FacultyApiService.BookingsByWeekResponse> response) {
                Log.d(TAG, "getBookingsByWeek: Response received. Code: " + response.code() + ", Success: " + response.isSuccessful());
                Log.d(TAG, "getBookingsByWeek: Response headers: " + response.headers());
                
                if (response.isSuccessful() && response.body() != null) {
                    FacultyApiService.BookingsByWeekResponse body = response.body();
                    Log.d(TAG, "getBookingsByWeek: Success - Total bookings: " + body.totalBookings);
                    Log.d(TAG, "getBookingsByWeek: Start date: " + body.startDate);
                    Log.d(TAG, "getBookingsByWeek: End date: " + body.endDate);
                    Log.d(TAG, "getBookingsByWeek: All bookings count: " + (body.allBookings != null ? body.allBookings.size() : "NULL"));
                    Log.d(TAG, "getBookingsByWeek: Bookings by date count: " + (body.bookingsByDate != null ? body.bookingsByDate.size() : "NULL"));
                    
                    if (body.allBookings != null) {
                        for (int i = 0; i < body.allBookings.size(); i++) {
                            Booking booking = body.allBookings.get(i);
                            Log.d(TAG, "getBookingsByWeek: Booking " + i + " - ID: " + booking.getBookingId() + ", Status: " + booking.getStatus());
                        }
                    }
                    
                    callback.onSuccess(body);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Could not read error body";
                    }
                    Log.e(TAG, "getBookingsByWeek: API call failed. Code: " + response.code() + ", Error: " + errorBody);
                    callback.onError("Không thể tải lịch hẹn theo tuần (HTTP " + response.code() + "): " + errorBody);
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingsByWeekResponse> call, Throwable t) {
                Log.e(TAG, "getBookingsByWeek: Network failure: " + t.getMessage());
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getBookingsByStatus(String status, Integer limit, FacultyApiCallback<FacultyApiService.BookingsByStatusResponse> callback) {
        apiService.getBookingsByStatus(status, limit).enqueue(new Callback<FacultyApiService.BookingsByStatusResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingsByStatusResponse> call, Response<FacultyApiService.BookingsByStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải lịch hẹn theo trạng thái");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingsByStatusResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Approve booking
    public void approveBooking(int bookingId, FacultyApiCallback<Booking> callback) {
        apiService.approveBooking(bookingId).enqueue(new Callback<FacultyApiService.BookingActionResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingActionResponse> call, Response<FacultyApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().booking);
                } else {
                    callback.onError("Không thể phê duyệt lịch hẹn");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingActionResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Cancel booking
    public void cancelBooking(int bookingId, String reason, FacultyApiCallback<Booking> callback) {
        FacultyApiService.CancelBookingRequest request = new FacultyApiService.CancelBookingRequest(reason);
        
        apiService.cancelBooking(bookingId, request).enqueue(new Callback<FacultyApiService.BookingActionResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingActionResponse> call, Response<FacultyApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().booking);
                } else {
                    callback.onError("Không thể hủy lịch hẹn");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingActionResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Reject booking
    public void rejectBooking(int bookingId, String reason, FacultyApiCallback<Booking> callback) {
        FacultyApiService.RejectBookingRequest request = new FacultyApiService.RejectBookingRequest(reason);
        
        apiService.rejectBooking(bookingId, request).enqueue(new Callback<FacultyApiService.BookingActionResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingActionResponse> call, Response<FacultyApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().booking);
                } else {
                    callback.onError("Không thể từ chối lịch hẹn");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingActionResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Mark booking completed
    public void markBookingCompleted(int bookingId, FacultyApiCallback<Booking> callback) {
        apiService.markBookingCompleted(bookingId).enqueue(new Callback<FacultyApiService.BookingActionResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.BookingActionResponse> call, Response<FacultyApiService.BookingActionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().booking);
                } else {
                    callback.onError("Không thể đánh dấu lịch hẹn hoàn thành");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.BookingActionResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Profile
    public void getProfile(FacultyApiCallback<User> callback) {
        apiService.getFacultyProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải thông tin profile");
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void updateProfile(String facultyName, String departmentId, String degree, 
                            String phoneNumber, String officeLocation, String avatar, 
                            FacultyApiCallback<User> callback) {
        FacultyApiService.UpdateFacultyProfileRequest request = new FacultyApiService.UpdateFacultyProfileRequest();
        request.facultyName = facultyName;
        request.departmentId = departmentId;
        request.degree = degree;
        request.phoneNumber = phoneNumber;
        request.officeLocation = officeLocation;
        request.avatar = avatar;
        
        apiService.updateFacultyProfile(request).enqueue(new Callback<FacultyApiService.ProfileResponse>() {
            @Override
            public void onResponse(Call<FacultyApiService.ProfileResponse> call, Response<FacultyApiService.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().user);
                } else {
                    callback.onError("Không thể cập nhật profile");
                }
            }
            
            @Override
            public void onFailure(Call<FacultyApiService.ProfileResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void createMultipleSlots(AddScheduleData scheduleData, FacultyApiCallback<JSONObject> callback) {
        // Chuẩn bị dữ liệu để gửi đi
        JSONObject requestBody = new JSONObject();
        try {
            JSONArray slotsArray = new JSONArray();
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Gửi lên server ở dạng UTC

            if (scheduleData.isSpecificDateMode()) {
                // Chế độ ngày cụ thể
                for (TimeSlot timeSlot : scheduleData.getTimeSlots()) {
                    if (timeSlot.isSelected()) {
                        // Kết hợp ngày đã chọn và giờ trong TimeSlot
                        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(scheduleData.getSelectedDate());
                        Calendar calendar = Calendar.getInstance();

                        String[] startTimeParts = timeSlot.getStartTime().split(":");
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
                        calendar.set(Calendar.SECOND, 0);
                        String fullStartTime = outputFormat.format(calendar.getTime());

                        String[] endTimeParts = timeSlot.getEndTime().split(":");
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
                        calendar.set(Calendar.SECOND, 0);
                        String fullEndTime = outputFormat.format(calendar.getTime());

                        JSONObject slotObject = new JSONObject();
                        slotObject.put("start_time", fullStartTime);
                        slotObject.put("end_time", fullEndTime);
                        slotsArray.put(slotObject);
                    }
                }
            } else {
                // Chế độ lịch cố định - sử dụng dayTimeSlots
                for (AddScheduleData.DayTimeSlot dayTimeSlot : scheduleData.getDayTimeSlots()) {
                    for (TimeSlot timeSlot : dayTimeSlot.getTimeSlots()) {
                        // Tạo ngày cho tuần hiện tại dựa trên dayOfWeek
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, dayTimeSlot.getDayOfWeek());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        String[] startTimeParts = timeSlot.getStartTime().split(":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
                        String fullStartTime = outputFormat.format(calendar.getTime());

                        String[] endTimeParts = timeSlot.getEndTime().split(":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
                        String fullEndTime = outputFormat.format(calendar.getTime());

                        JSONObject slotObject = new JSONObject();
                        slotObject.put("start_time", fullStartTime);
                        slotObject.put("end_time", fullEndTime);
                        slotsArray.put(slotObject);
                    }
                }
            }

            requestBody.put("slots", slotsArray);
            requestBody.put("max_students", scheduleData.getMaxStudents());
            requestBody.put("slot_duration", scheduleData.getSlotDuration());
            requestBody.put("notes", scheduleData.getNotes());

        } catch (JSONException | ParseException e) {
            Log.e("FacultyRepository", "Error creating JSON body for createMultipleSlots", e);
            callback.onError("Lỗi khi tạo yêu cầu: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
        Call<ResponseBody> call = apiService.createMultipleSlots(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        Log.d("FacultyRepository", "createMultipleSlots successful: " + responseString);
                        callback.onSuccess(jsonResponse);
                    } catch (Exception e) {
                        Log.e("FacultyRepository", "Error parsing success response in createMultipleSlots", e);
                        callback.onError("Lỗi đọc phản hồi từ server.");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("FacultyRepository", "createMultipleSlots failed: " + errorBody);
                        callback.onError(errorBody);
                    } catch (IOException e) {
                        Log.e("FacultyRepository", "Error reading error body in createMultipleSlots", e);
                        callback.onError("Lỗi không xác định từ server.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FacultyRepository", "API call failed for createMultipleSlots", t);
                callback.onError(t.getMessage());
            }
        });
    }
    
    public void createMonthlySchedule(AddScheduleData scheduleData, FacultyApiCallback<JSONObject> callback) {
        // Chuẩn bị dữ liệu để gửi đi
        JSONObject requestBody = new JSONObject();
        try {
            JSONArray slotsArray = new JSONArray();
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Gửi lên server ở dạng UTC

            // Chỉ xử lý cho chế độ lịch cố định
            for (AddScheduleData.DayTimeSlot dayTimeSlot : scheduleData.getDayTimeSlots()) {
                for (TimeSlot timeSlot : dayTimeSlot.getTimeSlots()) {
                    // Chuyển dayOfWeek (1=Thứ 2, ..., 7=Chủ nhật) sang Calendar.DAY_OF_WEEK (1=Chủ nhật, 2=Thứ 2, ..., 7=Thứ 7)
                    int calendarDayOfWeek = (dayTimeSlot.getDayOfWeek() % 7) + 1;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    String[] startTimeParts = timeSlot.getStartTime().split(":");
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
                    String fullStartTime = outputFormat.format(calendar.getTime());

                    String[] endTimeParts = timeSlot.getEndTime().split(":");
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
                    String fullEndTime = outputFormat.format(calendar.getTime());

                    JSONObject slotObject = new JSONObject();
                    slotObject.put("start_time", fullStartTime);
                    slotObject.put("end_time", fullEndTime);
                    slotsArray.put(slotObject);
                }
            }

            requestBody.put("slots", slotsArray);
            requestBody.put("max_students", scheduleData.getMaxStudents());
            requestBody.put("slot_duration", scheduleData.getSlotDuration());
            requestBody.put("notes", scheduleData.getNotes());
            requestBody.put("apply_monthly", scheduleData.isApplyMonthly());

            // Lấy tháng/năm từ scheduleData (AddScheduleData) thay vì Calendar.getInstance()
            requestBody.put("month", scheduleData.getMonth());
            requestBody.put("year", scheduleData.getYear());

        } catch (JSONException e) {
            Log.e("FacultyRepository", "Error creating JSON body for createMonthlySchedule", e);
            callback.onError("Lỗi khi tạo yêu cầu: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
        Call<ResponseBody> call = apiService.createMonthlySchedule(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        Log.d("FacultyRepository", "createMonthlySchedule successful: " + responseString);
                        callback.onSuccess(jsonResponse);
                    } catch (Exception e) {
                        Log.e("FacultyRepository", "Error parsing success response in createMonthlySchedule", e);
                        callback.onError("Lỗi đọc phản hồi từ server.");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("FacultyRepository", "createMonthlySchedule failed: " + errorBody);
                        callback.onError(errorBody);
                    } catch (IOException e) {
                        Log.e("FacultyRepository", "Error reading error body in createMonthlySchedule", e);
                        callback.onError("Lỗi không xác định từ server.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FacultyRepository", "API call failed for createMonthlySchedule", t);
                callback.onError(t.getMessage());
            }
        });
    }
    
    // Callback interface
    public interface FacultyApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
} 