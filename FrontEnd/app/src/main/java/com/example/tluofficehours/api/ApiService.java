package com.example.tluofficehours.api;

import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.LoginResponse;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.model.User;
import com.example.tluofficehours.model.UserProfile;
import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.Appointment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface ApiService {
    
    // ====== AUTHENTICATION APIs ======
    
    @POST("api/login")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LoginResponse> login(@Body LoginRequest request);

    @Multipart
    @POST("api/auth/register-faculty")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> registerFaculty(
        @Part("email") RequestBody email,
        @Part("password") RequestBody password,
        @Part("password_confirmation") RequestBody passwordConfirmation,
        @Part("faculty_name") RequestBody facultyName,
        @Part("department_id") RequestBody departmentId,
        @Part("degree") RequestBody degree,
        @Part("phone_number") RequestBody phoneNumber,
        @Part("office_room") RequestBody officeRoom,
        @Part MultipartBody.Part avatar
    );

    @Multipart
    @POST("api/auth/register-student")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> registerStudent(
        @Part("email") RequestBody email,
        @Part("password") RequestBody password,
        @Part("password_confirmation") RequestBody passwordConfirmation,
        @Part("StudentName") RequestBody studentName,
        @Part("StudentCode") RequestBody studentCode,
        @Part("ClassName") RequestBody className,
        @Part("PhoneNumber") RequestBody phoneNumber,
        @Part MultipartBody.Part avatar
    );

    // ====== COMMON APIs ======
    
    @GET("api/departments")
    @Headers({"Accept: application/json"})
    Call<List<Department>> getDepartments();

    @GET("api/user/profile")
    @Headers({"Accept: application/json"})
    Call<UserProfile> getUserProfile();

    @GET("api/student/profile")
    @Headers({"Accept: application/json"})
    Call<StudentProfile> getStudentProfile();

    @GET("api/faculty/profile")
    @Headers({"Accept: application/json"})
    Call<FacultyProfile> getFacultyProfile();

    @GET("api/student/featured-teachers")
    @Headers({"Accept: application/json"})
    Call<List<FacultyProfile>> getFeaturedTeachers();

    @GET("api/student/teachers-by-department/{departmentId}")
    @Headers({"Accept: application/json"})
    Call<List<FacultyProfile>> getTeachersByDepartment(@Path("departmentId") String departmentId);

    @PUT("api/student/profile")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    Call<ResponseBody> updateStudentProfileJson(@Body Map<String, String> profileData);

    @GET("api/notifications")
    @Headers({"Accept: application/json"})
    Call<List<com.example.tluofficehours.model.Notification>> getNotifications();

    @GET("api/student/search-teachers")
    @Headers({"Accept: application/json"})
    Call<List<FacultyProfile>> searchTeachers(@Query("query") String query);

    @GET("api/student/teacher/{facultyUserId}")
    @Headers({"Accept: application/json"})
    Call<FacultyProfile> getTeacherDetail(@Path("facultyUserId") String facultyUserId);

    @GET("api/faculty/{facultyUserId}/available-slots")
    @Headers({"Accept: application/json"})
    Call<List<AvailableSlot>> getAvailableSlots(
        @Path("facultyUserId") String facultyUserId,
        @Query("date") String date
    );

    @POST("api/student/book-appointment")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> bookAppointment(@Body Map<String, Object> bookingData);

    @GET("api/student/appointments")
    @Headers({"Accept: application/json"})
    Call<List<Appointment>> getStudentAppointments();

    @POST("api/student/appointments/{id}/cancel")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> cancelAppointment(
        @Path("id") int appointmentId,
        @Body Map<String, String> body
    );

    @Multipart
    @PUT("api/faculty/profile")
    @Headers({"Accept: application/json"})
    Call<ResponseBody> updateFacultyProfile(
        @Part("faculty_name") RequestBody facultyName,
        @Part("department_id") RequestBody departmentId,
        @Part("degree") RequestBody degree,
        @Part("phone_number") RequestBody phoneNumber,
        @Part("office_location") RequestBody officeLocation,
        @Part MultipartBody.Part avatar
    );

    @POST("api/faculty/slots/multiple")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ResponseBody> createMultipleSlots(@Body Object body);

    @POST("api/faculty/slots/monthly")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ResponseBody> createMonthlySchedule(@Body Object body);

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
    Call<User> getFacultyProfileRaw();

    @PUT("api/faculty/profile")
    Call<ProfileResponse> updateFacultyProfileRaw(@Body UpdateFacultyProfileRequest request);

    // ==================== FACULTY API RESPONSE/REQUEST CLASSES ====================
    public static class DashboardResponse {
        @com.google.gson.annotations.SerializedName("total_slots")
        public int totalSlots;
        @com.google.gson.annotations.SerializedName("available_slots")
        public int availableSlots;
        @com.google.gson.annotations.SerializedName("total_bookings")
        public int totalBookings;
        @com.google.gson.annotations.SerializedName("pending_bookings")
        public int pendingBookings;
        @com.google.gson.annotations.SerializedName("confirmed_bookings")
        public int confirmedBookings;
        @com.google.gson.annotations.SerializedName("recent_bookings")
        public java.util.List<com.example.tluofficehours.model.Booking> recentBookings;
        @com.google.gson.annotations.SerializedName("upcoming_slots")
        public java.util.List<com.example.tluofficehours.model.AvailableSlot> upcomingSlots;
    }
    public static class BookingsByDateResponse {
        @com.google.gson.annotations.SerializedName("date")
        public String date;
        @com.google.gson.annotations.SerializedName("total_bookings")
        public int totalBookings;
        @com.google.gson.annotations.SerializedName("bookings")
        public java.util.List<com.example.tluofficehours.model.Booking> bookings;
    }
    public static class BookingsByWeekResponse {
        @com.google.gson.annotations.SerializedName("start_date")
        public String startDate;
        @com.google.gson.annotations.SerializedName("end_date")
        public String endDate;
        @com.google.gson.annotations.SerializedName("total_bookings")
        public int totalBookings;
        @com.google.gson.annotations.SerializedName("bookings_by_date")
        public java.util.Map<String, java.util.List<com.example.tluofficehours.model.Booking>> bookingsByDate;
        @com.google.gson.annotations.SerializedName("all_bookings")
        public java.util.List<com.example.tluofficehours.model.Booking> allBookings;
    }
    public static class BookingsByStatusResponse {
        @com.google.gson.annotations.SerializedName("status")
        public String status;
        @com.google.gson.annotations.SerializedName("total_bookings")
        public int totalBookings;
        @com.google.gson.annotations.SerializedName("bookings")
        public java.util.List<com.example.tluofficehours.model.Booking> bookings;
    }
    public static class BookingActionResponse {
        @com.google.gson.annotations.SerializedName("message")
        public String message;
        @com.google.gson.annotations.SerializedName("booking")
        public com.example.tluofficehours.model.Booking booking;
    }
    public static class SlotResponse {
        @com.google.gson.annotations.SerializedName("message")
        public String message;
        @com.google.gson.annotations.SerializedName("slot")
        public com.example.tluofficehours.model.AvailableSlot slot;
    }
    public static class MessageResponse {
        @com.google.gson.annotations.SerializedName("message")
        public String message;
    }
    public static class ProfileResponse {
        @com.google.gson.annotations.SerializedName("message")
        public String message;
        @com.google.gson.annotations.SerializedName("user")
        public com.example.tluofficehours.model.User user;
    }
    public static class CreateSlotRequest {
        @com.google.gson.annotations.SerializedName("start_time")
        public String startTime;
        @com.google.gson.annotations.SerializedName("end_time")
        public String endTime;
        @com.google.gson.annotations.SerializedName("max_students")
        public int maxStudents;
        @com.google.gson.annotations.SerializedName("definition_id")
        public Integer definitionId;
        public CreateSlotRequest(String startTime, String endTime, int maxStudents, Integer definitionId) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.maxStudents = maxStudents;
            this.definitionId = definitionId;
        }
    }
    public static class UpdateSlotRequest {
        @com.google.gson.annotations.SerializedName("start_time")
        public String startTime;
        @com.google.gson.annotations.SerializedName("end_time")
        public String endTime;
        @com.google.gson.annotations.SerializedName("max_students")
        public int maxStudents;
        @com.google.gson.annotations.SerializedName("is_available")
        public boolean isAvailable;
        @com.google.gson.annotations.SerializedName("definition_id")
        public Integer definitionId;
    }
    public static class RejectBookingRequest {
        @com.google.gson.annotations.SerializedName("reason")
        public String reason;
        public RejectBookingRequest(String reason) { this.reason = reason; }
    }
    public static class CancelBookingRequest {
        @com.google.gson.annotations.SerializedName("reason")
        public String reason;
        public CancelBookingRequest(String reason) { this.reason = reason; }
    }
    public static class UpdateFacultyProfileRequest {
        @com.google.gson.annotations.SerializedName("faculty_name")
        public String facultyName;
        @com.google.gson.annotations.SerializedName("department_id")
        public String departmentId;
        @com.google.gson.annotations.SerializedName("degree")
        public String degree;
        @com.google.gson.annotations.SerializedName("phone_number")
        public String phoneNumber;
        @com.google.gson.annotations.SerializedName("office_location")
        public String officeLocation;
        @com.google.gson.annotations.SerializedName("avatar")
        public String avatar;
    }
}
