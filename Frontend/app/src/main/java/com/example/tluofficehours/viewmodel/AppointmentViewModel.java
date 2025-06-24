package com.example.tluofficehours.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tluofficehours.model.Appointment;
import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.repository.AppointmentRepository;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unified ViewModel for all appointment-related operations
 * Consolidates functionality from:
 * - BookAppointmentViewModel
 * - MyAppointmentViewModel  
 * - AppointmentDetailViewModel
 */
public class AppointmentViewModel extends ViewModel {
    private AppointmentRepository appointmentRepository;
    
    // LiveData for different appointment operations
    private MutableLiveData<List<Appointment>> appointments = new MutableLiveData<>();
    private MutableLiveData<List<AvailableSlot>> availableSlots = new MutableLiveData<>();
    private MutableLiveData<Appointment> appointmentDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> bookingSuccess = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> cancelSuccess = new MutableLiveData<>(false);
    
    // LiveData for BookingSuccess screen
    private MutableLiveData<String> teacherName = new MutableLiveData<>();
    private MutableLiveData<String> date = new MutableLiveData<>();
    private MutableLiveData<String> time = new MutableLiveData<>();
    private MutableLiveData<String> room = new MutableLiveData<>();
    private MutableLiveData<String> contactEmail = new MutableLiveData<>();

    public AppointmentViewModel() {
        appointmentRepository = new AppointmentRepository();
    }

    // ==================== STUDENT APPOINTMENTS ====================
    
    /**
     * Load student appointments (for MyAppointment screen)
     */
    public void loadStudentAppointments() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        appointmentRepository.getStudentAppointments(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    appointments.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách lịch hẹn");
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== BOOKING APPOINTMENTS ====================
    
    /**
     * Fetch available slots for a faculty member on a specific date
     */
    public void fetchAvailableSlots(String facultyUserId, String date) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        appointmentRepository.getAvailableSlots(facultyUserId, date, new Callback<List<AvailableSlot>>() {
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

    /**
     * Book an appointment
     */
    public void bookAppointment(java.util.Map<String, Object> bookingData) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        bookingSuccess.setValue(false);

        appointmentRepository.bookAppointment(bookingData, new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    bookingSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Đặt lịch thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== APPOINTMENT DETAILS ====================
    
    /**
     * Cancel an appointment (for AppointmentDetail screen)
     */
    public void cancelAppointment(int appointmentId, String reason) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        cancelSuccess.setValue(false);

        appointmentRepository.cancelAppointment(appointmentId, reason, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    cancelSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Hủy lịch thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== LIVE DATA GETTERS ====================
    
    public LiveData<List<Appointment>> getAppointments() {
        return appointments;
    }

    public LiveData<List<AvailableSlot>> getAvailableSlots() {
        return availableSlots;
    }

    public LiveData<Appointment> getAppointmentDetail() {
        return appointmentDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getBookingSuccess() {
        return bookingSuccess;
    }

    public LiveData<Boolean> getCancelSuccess() {
        return cancelSuccess;
    }

    public LiveData<String> getTeacherName() {
        return teacherName;
    }

    public LiveData<String> getDate() {
        return date;
    }

    public LiveData<String> getTime() {
        return time;
    }

    public LiveData<String> getRoom() {
        return room;
    }

    public LiveData<String> getContactEmail() {
        return contactEmail;
    }

    // ==================== BOOKING SUCCESS ====================
    
    /**
     * Set booking data for BookingSuccess screen
     */
    public void setBookingData(String teacherName, String date, String time, String room, String contactEmail) {
        this.teacherName.setValue(teacherName);
        this.date.setValue(date);
        this.time.setValue(time);
        this.room.setValue(room);
        this.contactEmail.setValue(contactEmail);
    }
    
    /**
     * Load appointments (alias for loadStudentAppointments for backward compatibility)
     */
    public void loadAppointments() {
        loadStudentAppointments();
    }
} 