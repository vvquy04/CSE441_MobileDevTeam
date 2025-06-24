package com.example.tluofficehours.api;

import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public interface FacultyApiService {
    
    // ==================== FACULTY DASHBOARD ====================
    
    @GET("api/faculty/dashboard")
    Call<DashboardResponse> getFacultyDashboard();
    
    // ==================== AVAILABLE SLOTS MANAGEMENT ====================
    
    @GET("api/faculty/slots")
    Call<List<AvailableSlot>> getFacultySlots();
    
    @POST("api/faculty/slots")
    Call<SlotResponse> createFacultySlot(@Body CreateSlotRequest request);
    
    @PUT("api/faculty/slots/{id}")
    Call<SlotResponse> updateFacultySlot(@Path("id") int slotId, @Body UpdateSlotRequest request);
    
    @DELETE("api/faculty/slots/{id}")
    Call<MessageResponse> deleteFacultySlot(@Path("id") int slotId);
    
    @POST("api/faculty/slots/{id}/toggle")
    Call<SlotResponse> toggleFacultySlotAvailability(@Path("id") int slotId);
    
    @POST("api/faculty/slots/multiple")
    Call<ResponseBody> createMultipleSlots(@Body RequestBody body);

    @POST("api/faculty/slots/monthly")
    Call<ResponseBody> createMonthlySchedule(@Body RequestBody body);
    
    @GET("faculty/dashboard")
    Call<ResponseBody> getDashboard();
    
    // ==================== BOOKING MANAGEMENT ====================
    
    @GET("api/faculty/bookings")
    Call<List<Booking>> getFacultyBookings();
    
    @GET("api/faculty/bookings/pending")
    Call<List<Booking>> getFacultyPendingBookings();
    
    @GET("api/faculty/bookings/confirmed")
    Call<List<Booking>> getFacultyConfirmedBookings();
    
    // ==================== NEW BOOKING APIs ====================
    
    @GET("api/faculty/bookings/by-date")
    Call<BookingsByDateResponse> getBookingsByDate(
        @Query("date") String date,
        @Query("status") String status
    );
    
    @GET("api/faculty/bookings/by-week")
    Call<BookingsByWeekResponse> getBookingsByWeek(
        @Query("start_date") String startDate,
        @Query("end_date") String endDate,
        @Query("status") String status
    );
    
    @GET("api/faculty/bookings/by-status")
    Call<BookingsByStatusResponse> getBookingsByStatus(
        @Query("status") String status,
        @Query("limit") Integer limit
    );
    
    // ==================== BOOKING ACTIONS ====================
    
    @POST("api/faculty/bookings/{id}/approve")
    Call<BookingActionResponse> approveBooking(@Path("id") int bookingId);
    
    @POST("api/faculty/bookings/{id}/reject")
    Call<BookingActionResponse> rejectBooking(
        @Path("id") int bookingId, 
        @Body RejectBookingRequest request
    );
    
    @POST("api/faculty/bookings/{id}/cancel")
    Call<BookingActionResponse> cancelBooking(
        @Path("id") int bookingId, 
        @Body CancelBookingRequest request
    );
    
    @POST("api/faculty/bookings/{id}/complete")
    Call<BookingActionResponse> markBookingCompleted(@Path("id") int bookingId);
    
    // ==================== FACULTY PROFILE ====================
    
    @GET("api/faculty/profile")
    Call<User> getFacultyProfile();
    
    @PUT("api/faculty/profile")
    Call<ProfileResponse> updateFacultyProfile(@Body UpdateFacultyProfileRequest request);

    // ==================== REQUEST/RESPONSE CLASSES ====================
    
    // Faculty Dashboard Response
    class DashboardResponse {
        @SerializedName("total_slots")
        public int totalSlots;
        @SerializedName("available_slots")
        public int availableSlots;
        @SerializedName("total_bookings")
        public int totalBookings;
        @SerializedName("pending_bookings")
        public int pendingBookings;
        @SerializedName("confirmed_bookings")
        public int confirmedBookings;
        @SerializedName("recent_bookings")
        public List<Booking> recentBookings;
        @SerializedName("upcoming_slots")
        public List<AvailableSlot> upcomingSlots;
    }
    
    // Bookings by Date Response
    class BookingsByDateResponse {
        @SerializedName("date")
        public String date;
        @SerializedName("total_bookings")
        public int totalBookings;
        @SerializedName("bookings")
        public List<Booking> bookings;
    }
    
    // Bookings by Week Response
    class BookingsByWeekResponse {
        @SerializedName("start_date")
        public String startDate;
        @SerializedName("end_date")
        public String endDate;
        @SerializedName("total_bookings")
        public int totalBookings;
        @SerializedName("bookings_by_date")
        public Map<String, List<Booking>> bookingsByDate;
        @SerializedName("all_bookings")
        public List<Booking> allBookings;
    }
    
    // Bookings by Status Response
    class BookingsByStatusResponse {
        @SerializedName("status")
        public String status;
        @SerializedName("total_bookings")
        public int totalBookings;
        @SerializedName("bookings")
        public List<Booking> bookings;
    }
    
    // Booking Action Response
    class BookingActionResponse {
        @SerializedName("message")
        public String message;
        @SerializedName("booking")
        public Booking booking;
    }
    
    // Slot Response
    class SlotResponse {
        @SerializedName("message")
        public String message;
        @SerializedName("slot")
        public AvailableSlot slot;
    }
    
    // Message Response
    class MessageResponse {
        @SerializedName("message")
        public String message;
    }
    
    // Profile Response
    class ProfileResponse {
        @SerializedName("message")
        public String message;
        @SerializedName("user")
        public User user;
    }
    
    // Slot Management Requests
    class CreateSlotRequest {
        @SerializedName("start_time")
        public String startTime;
        @SerializedName("end_time")
        public String endTime;
        @SerializedName("max_students")
        public int maxStudents;
        @SerializedName("definition_id")
        public Integer definitionId;
        
        public CreateSlotRequest(String startTime, String endTime, int maxStudents, Integer definitionId) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.maxStudents = maxStudents;
            this.definitionId = definitionId;
        }
    }
    
    class UpdateSlotRequest {
        @SerializedName("start_time")
        public String startTime;
        @SerializedName("end_time")
        public String endTime;
        @SerializedName("max_students")
        public int maxStudents;
        @SerializedName("is_available")
        public boolean isAvailable;
        @SerializedName("definition_id")
        public Integer definitionId;
    }
    
    // Booking Action Requests
    class RejectBookingRequest {
        @SerializedName("reason")
        public String reason;
        
        public RejectBookingRequest(String reason) {
            this.reason = reason;
        }
    }
    
    class CancelBookingRequest {
        @SerializedName("reason")
        public String reason;
        
        public CancelBookingRequest(String reason) {
            this.reason = reason;
        }
    }
    
    // Profile Update Requests
    class UpdateFacultyProfileRequest {
        @SerializedName("faculty_name")
        public String facultyName;
        @SerializedName("department_id")
        public String departmentId;
        @SerializedName("degree")
        public String degree;
        @SerializedName("phone_number")
        public String phoneNumber;
        @SerializedName("office_location")
        public String officeLocation;
        @SerializedName("avatar")
        public String avatar;
    }
} 