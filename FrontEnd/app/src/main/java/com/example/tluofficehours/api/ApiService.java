package com.example.tluofficehours.api;

import com.example.tluofficehours.model.RegisterStudentRequest;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.LoginResponse;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.model.TimeSlot;
import com.example.tluofficehours.model.UserProfile;
import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.model.Teacher;
import com.example.tluofficehours.model.Appointment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
    @Multipart
    @POST("api/auth/register-faculty")
    @Headers({
        "Accept: application/json"
    })
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

    @GET("api/departments")
    @Headers({
        "Accept: application/json"
    })
    Call<List<Department>> getDepartments();

    @POST("api/login")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LoginResponse> login(@Body LoginRequest request);

    @Multipart
    @POST("api/auth/register-student")
    @Headers({
        "Accept: application/json"
    })
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

    @GET("api/user/profile")
    @Headers({
        "Accept: application/json"
    })
    Call<UserProfile> getUserProfile();

    @GET("api/student/profile")
    @Headers({
        "Accept: application/json"
    })
    Call<StudentProfile> getStudentProfile();

    @GET("api/faculty/profile")
    @Headers({
        "Accept: application/json"
    })
    Call<UserProfile> getFacultyProfile();

    @GET("api/student/featured-teachers")
    @Headers({
        "Accept: application/json"
    })
    Call<List<Teacher>> getFeaturedTeachers();

    @GET("api/student/teachers-by-department/{departmentId}")
    @Headers({
        "Accept: application/json"
    })
    Call<List<Teacher>> getTeachersByDepartment(@Path("departmentId") String departmentId);

    // Commented out multipart method since JSON method is working
    /*
    @Multipart
    @PUT("api/student/profile")
    @Headers({
        "Accept: application/json"
    })
    Call<ResponseBody> updateStudentProfile(
        @Part("StudentName") RequestBody studentName,
        @Part("PhoneNumber") RequestBody phoneNumber,
        @Part("ClassName") RequestBody className,
        @Part MultipartBody.Part avatar
    );
    */

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
    Call<List<Teacher>> searchTeachers(@Query("query") String query);

    @GET("api/student/teacher/{facultyUserId}")
    @Headers({"Accept: application/json"})
    Call<Teacher> getTeacherDetail(@Path("facultyUserId") String facultyUserId);

    @GET("api/faculty/{facultyUserId}/available-slots")
    @Headers({
        "Accept: application/json"
    })
    Call<List<TimeSlot>> getAvailableSlots(
        @Path("facultyUserId") String facultyUserId,
        @Query("date") String date
    );

    @POST("api/student/book-appointment")
    @Headers({"Accept: application/json"})
    Call<okhttp3.ResponseBody> bookAppointment(
        @Body java.util.Map<String, Object> bookingData
    );

    @GET("api/student/appointments")
    @Headers({"Accept: application/json"})
    Call<java.util.List<Appointment>> getStudentAppointments();

    @POST("api/student/appointments/{id}/cancel")
    @Headers({"Accept: application/json"})
    Call<okhttp3.ResponseBody> cancelAppointment(
        @Path("id") int appointmentId,
        @Body java.util.Map<String, String> body
    );
}
