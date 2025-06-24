package com.example.tluofficehours.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.repository.FacultyRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FacultyCalendarViewModel extends AndroidViewModel {
    private static final String TAG = "FacultyCalendarViewModel";
    private FacultyRepository repository;

    private final MutableLiveData<ApiService.BookingsByWeekResponse> allBookingsInPeriod = new MutableLiveData<>();

    // LiveData chứa TẤT CẢ các cuộc hẹn đã tải về từ API (ví dụ: cho cả tuần)
    private final MutableLiveData<List<Booking>> allBookingsList = new MutableLiveData<>();
    // LiveData chứa trạng thái lọc hiện tại ("ALL", "pending", "confirmed", etc.)
    private final MutableLiveData<String> currentFilterStatus = new MutableLiveData<>("ALL");
    // LiveData chứa ngày được chọn hiện tại
    private final MutableLiveData<Date> currentSelectedDateLiveData = new MutableLiveData<>();

    // MediatorLiveData kết hợp allBookingsList, currentFilterStatus và currentSelectedDateLiveData
    // để xuất ra danh sách lịch hẹn cuối cùng đã được lọc
    private final MediatorLiveData<List<Booking>> filteredAppointments = new MediatorLiveData<>();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    private final MutableLiveData<List<Date>> eventDays = new MutableLiveData<>();

    public FacultyCalendarViewModel(@NonNull Application application) {
        super(application);
        repository = new FacultyRepository();

        // Logic của MediatorLiveData:
        // Sẽ gọi hàm applyFilters mỗi khi một trong 3 LiveData nguồn thay đổi
        filteredAppointments.addSource(allBookingsList, bookings -> applyFilters());
        filteredAppointments.addSource(currentFilterStatus, status -> applyFilters());
        filteredAppointments.addSource(currentSelectedDateLiveData, date -> applyFilters());
    }

    // LiveData getters
    public LiveData<List<Booking>> getFilteredAppointments() { return filteredAppointments; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<List<Date>> getEventDays() { return eventDays; }
    public LiveData<ApiService.BookingsByWeekResponse> getAllBookingsInPeriod() { return allBookingsInPeriod; }

    /**
     * Tải tất cả lịch hẹn trong một khoảng thời gian nhất định (ví dụ: tuần hiện tại).
     * Hàm này nên được gọi khi tuần hiển thị thay đổi.
     * Dữ liệu sẽ được lưu vào allBookingsList và sau đó MediatorLiveData sẽ tự lọc.
     */
    public void loadAppointmentsForWeek(Date dateInWeek, String filterStatus) {
        Log.d(TAG, "loadAppointmentsForWeek: Loading appointments for week containing date: " + dateInWeek + ", filter: " + filterStatus);
        isLoading.setValue(true);

        currentSelectedDateLiveData.setValue(dateInWeek);
        currentFilterStatus.setValue(filterStatus);

        Calendar calendar = Calendar.getInstance(new Locale("vi", "VN"));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(dateInWeek);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDateOfWeek = calendar.getTime();
        calendar.add(Calendar.DATE, 6);
        Date endDateOfWeek = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = dateFormat.format(startDateOfWeek);
        String endDateStr = dateFormat.format(endDateOfWeek);

        repository.getBookingsByWeek(startDateStr, endDateStr, null).observeForever(result -> {
            if (result != null) {
                allBookingsList.setValue(result);
                isLoading.setValue(false);
            } else {
                allBookingsList.setValue(new ArrayList<>());
                isLoading.setValue(false);
                errorMessage.setValue("Không thể tải dữ liệu lịch hẹn tuần");
            }
        });
    }

    /**
     * Tải lịch hẹn theo ngày cụ thể
     */
    public void loadAppointmentsForDate(Date date, String filterStatus) {
        Log.d(TAG, "loadAppointmentsForDate: Loading appointments for date: " + date + ", filter: " + filterStatus);
        isLoading.setValue(true);

        currentSelectedDateLiveData.setValue(date);
        currentFilterStatus.setValue(filterStatus);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = dateFormat.format(date);

        repository.getBookingsByDate(dateStr, filterStatus.equals("ALL") ? null : filterStatus).observeForever(result -> {
            if (result != null) {
                allBookingsList.setValue(result);
                isLoading.setValue(false);
            } else {
                allBookingsList.setValue(new ArrayList<>());
                isLoading.setValue(false);
                errorMessage.setValue("Không thể tải dữ liệu lịch hẹn ngày");
            }
        });
    }
    
    /**
     * Tải lịch hẹn theo trạng thái
     */
    public void loadAppointmentsByStatus(String status, Integer limit) {
        Log.d(TAG, "loadAppointmentsByStatus: Loading appointments with status: " + status + ", limit: " + limit);
        isLoading.setValue(true);

        repository.getBookingsByStatus(status, limit).observeForever(result -> {
            if (result != null) {
                allBookingsList.setValue(result);
                isLoading.setValue(false);
            } else {
                allBookingsList.setValue(new ArrayList<>());
                isLoading.setValue(false);
                errorMessage.setValue("Không thể tải dữ liệu lịch hẹn theo trạng thái");
            }
        });
    }

    /**
     * Hàm này được gọi khi ngày được chọn hoặc trạng thái lọc thay đổi
     * Nó sẽ áp dụng các bộ lọc lên allBookingsList và cập nhật filteredAppointments.
     */
    private void applyFilters() {
        Log.d(TAG, "applyFilters: Applying filters...");
        List<Booking> allBookings = allBookingsList.getValue();
        String status = currentFilterStatus.getValue();
        Date selectedDate = currentSelectedDateLiveData.getValue();

        if (allBookings == null || selectedDate == null || status == null) {
            filteredAppointments.setValue(new ArrayList<>()); // Trả về rỗng nếu chưa đủ dữ liệu
            Log.d(TAG, "applyFilters: Missing data for filtering. Bookings: " + (allBookings != null) + ", Date: " + (selectedDate != null) + ", Status: " + (status != null));
            return;
        }

        List<Booking> tempFilteredList = new ArrayList<>();
        SimpleDateFormat debugFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        for (Booking booking : allBookings) {
            String bookingTime = booking.getBookingTime();
            boolean isSame = false;
            try {
                isSame = isSameDay(bookingTime, selectedDate);
            } catch (Exception e) {
                Log.e(TAG, "applyFilters: Error parsing bookingTime: " + bookingTime + ", selectedDate: " + debugFormat.format(selectedDate) + ", error: " + e.getMessage());
            }
            Log.d(TAG, "applyFilters: BookingId=" + booking.getBookingId() + ", bookingTime=" + bookingTime + ", selectedDate=" + debugFormat.format(selectedDate) + ", isSameDay=" + isSame);
            if (isSame) {
                tempFilteredList.add(booking);
            }
        }
        Log.d(TAG, "applyFilters: Filtered by date (" + selectedDate + "): " + tempFilteredList.size() + " bookings.");

        if ("ALL".equals(status)) {
            filteredAppointments.setValue(tempFilteredList);
            Log.d(TAG, "applyFilters: Final filtered list (ALL): " + tempFilteredList.size());
            return;
        }

        if ("REJECTED_CANCELLED".equals(status)) {
            List<Booking> finalFiltered = new ArrayList<>();
            for (Booking b : tempFilteredList) {
                if ("rejected".equals(b.getStatus()) || "cancelled".equals(b.getStatus())) {
                    finalFiltered.add(b);
                }
            }
            filteredAppointments.setValue(finalFiltered);
            Log.d(TAG, "applyFilters: Final filtered list (REJECTED_CANCELLED): " + finalFiltered.size());
            return;
        }

        List<Booking> finalFiltered = new ArrayList<>();
        for (Booking b : tempFilteredList) {
            if (status.equalsIgnoreCase(b.getStatus())) {
                finalFiltered.add(b);
            }
        }
        filteredAppointments.setValue(finalFiltered);
        Log.d(TAG, "applyFilters: Final filtered list (" + status + "): " + finalFiltered.size());
    }

    // Setter cho ngày được chọn (sẽ kích hoạt MediatorLiveData)
    public void setCurrentSelectedDate(Date date) {
        if (!isSameDay(currentSelectedDateLiveData.getValue(), date)) {
            currentSelectedDateLiveData.setValue(date);
            Log.d(TAG, "setCurrentSelectedDate: Selected date updated to: " + date);
        }
    }

    // Setter cho trạng thái lọc (sẽ kích hoạt MediatorLiveData)
    public void setFilterStatus(String status) {
        if (!currentFilterStatus.getValue().equals(status)) {
            currentFilterStatus.setValue(status);
            Log.d(TAG, "setFilterStatus: Filter status updated to: " + status);
        }
    }

    // Helper method để kiểm tra cùng ngày (không quan tâm giờ, phút, giây)
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Overloaded isSameDay để xử lý String bookingTime từ API
    private boolean isSameDay(String bookingTimeString, Date targetDate) {
        try {
            Date bookingDate = parseBookingTime(bookingTimeString);
            // Convert cả hai về UTC để so sánh ngày
            Calendar cal1 = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
            cal1.setTime(bookingDate);
            Calendar cal2 = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
            cal2.setTime(targetDate);
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } catch (Exception e) {
            Log.e(TAG, "isSameDay: Error parsing booking time string: " + bookingTimeString + " - " + e.getMessage());
            return false;
        }
    }

    // Helper method để parse booking time string, robust hơn, chấp nhận thiếu Z hoặc có offset
    private Date parseBookingTime(String isoTime) throws Exception {
        if (isoTime == null || isoTime.isEmpty()) {
            throw new Exception("Input date string is null or empty");
        }
        // Chuẩn hóa: Nếu thiếu Z và không có offset, thêm Z vào cuối
        if (!isoTime.endsWith("Z") && !isoTime.matches(".*[+-][0-9]{2}:[0-9]{2}$")) {
            isoTime = isoTime + "Z";
        }
        // Handle microsecond precision from server
        if (isoTime.contains(".")) {
            int dotIndex = isoTime.indexOf('.');
            int zIndex = isoTime.indexOf('Z', dotIndex);
            if (zIndex > dotIndex && (zIndex - dotIndex) > 4) {
                isoTime = isoTime.substring(0, dotIndex + 4) + "Z";
            }
        }
        // First, try with milliseconds
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return isoFormat.parse(isoTime);
        } catch (java.text.ParseException e) {
            // Fallback to format without milliseconds
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return isoFormat.parse(isoTime);
        }
    }


    // Approve booking with reload parameters
    public void approveBooking(int bookingId, Date selectedDate, String filterStatus) {
        isLoading.setValue(true);
        repository.approveBooking(bookingId).observeForever(result -> {
            if (result != null) {
                successMessage.setValue("Đã xác nhận lịch hẹn thành công!");
                loadAppointmentsForWeek(selectedDate, filterStatus);
            } else {
                errorMessage.setValue("Không thể xác nhận lịch hẹn");
            }
            isLoading.setValue(false);
        });
    }

    // Overloaded method for backward compatibility (nếu có nơi nào vẫn gọi hàm cũ)
    public void approveBooking(int bookingId) {
        approveBooking(bookingId, currentSelectedDateLiveData.getValue(), currentFilterStatus.getValue());
    }

    // Cancel booking with reload parameters
    public void cancelBooking(int bookingId, String reason, Date selectedDate, String filterStatus) {
        isLoading.setValue(true);
        repository.cancelBooking(bookingId, reason).observeForever(result -> {
            if (result != null) {
                successMessage.setValue("Đã hủy lịch hẹn thành công!");
                loadAppointmentsForWeek(selectedDate, filterStatus);
            } else {
                errorMessage.setValue("Không thể hủy lịch hẹn");
            }
            isLoading.setValue(false);
        });
    }

    // Overloaded method for backward compatibility
    public void cancelBooking(int bookingId, String reason) {
        cancelBooking(bookingId, reason, currentSelectedDateLiveData.getValue(), currentFilterStatus.getValue());
    }

    // Reject booking with reload parameters
    public void rejectBooking(int bookingId, String reason, Date selectedDate, String filterStatus) {
        isLoading.setValue(true);
        repository.rejectBooking(bookingId, reason).observeForever(result -> {
            if (result != null) {
                successMessage.setValue("Đã từ chối lịch hẹn thành công!");
                loadAppointmentsForWeek(selectedDate, filterStatus);
            } else {
                errorMessage.setValue("Không thể từ chối lịch hẹn");
            }
            isLoading.setValue(false);
        });
    }

    // Overloaded method for backward compatibility
    public void rejectBooking(int bookingId, String reason) {
        rejectBooking(bookingId, reason, currentSelectedDateLiveData.getValue(), currentFilterStatus.getValue());
    }

    // Mark booking as completed with reload parameters
    public void markBookingCompleted(int bookingId, Date selectedDate, String filterStatus) {
        isLoading.setValue(true);
        repository.markBookingCompleted(bookingId).observeForever(result -> {
            if (result != null) {
                successMessage.setValue("Đã đánh dấu hoàn thành!");
                loadAppointmentsForWeek(selectedDate, filterStatus);
            } else {
                errorMessage.setValue("Không thể đánh dấu hoàn thành");
            }
            isLoading.setValue(false);
        });
    }

    // Overloaded method for backward compatibility
    public void markBookingCompleted(int bookingId) {
        markBookingCompleted(bookingId, currentSelectedDateLiveData.getValue(), currentFilterStatus.getValue());
    }

    public void getBookingDetails(int bookingId) {
        successMessage.setValue("Chuyển đến chi tiết lịch hẹn");
    }

    // Clear messages
    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void clearSuccessMessage() {
        successMessage.setValue(null);
    }

    // Format date for display (dùng cho tuần hiển thị)
    public String formatDateForDisplay(Date date) {
        Calendar cal = Calendar.getInstance(new Locale("vi", "VN"));
        cal.setFirstDayOfWeek(Calendar.MONDAY); // Đảm bảo tuần bắt đầu từ Thứ Hai
        cal.setTime(date);

        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);

        // Get the start of the week (Monday)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = cal.getTime();

        // Get the end of the week (Sunday)
        cal.add(Calendar.DATE, 6);
        Date endDate = cal.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        return "Tuần " + weekOfYear + ": " + startDateStr + " - " + endDateStr;
    }

    // Format time for display (giống Adapter, dùng cho item lịch hẹn)
    public String formatTimeForDisplay(String bookingTime) {
        try {
            Date date = parseBookingTime(bookingTime);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                outputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                return outputFormat.format(date);
            }
            return bookingTime;
        } catch (Exception e) {
            Log.e(TAG, "formatTimeForDisplay: Error parsing time: " + bookingTime + " - " + e.getMessage());
            return bookingTime;
        }
    }

    // Get status color based on status
    public int getStatusColor(String status) {
        if (status == null) {
            return 0xFFF5F5F5; // Màu xám nhạt mặc định
        }
        switch (status) {
            case "confirmed":
                return 0xFFE8F5E8; // Màu xanh lá cây nhạt
            case "pending":
                return 0xFFFFF8E1; // Màu cam nhạt
            case "completed":
                return 0xFFE1F5FE; // Màu xanh dương nhạt
            case "cancelled":
            case "rejected":
                return 0xFFFFEBEE; // Màu đỏ nhạt
            default:
                return 0xFFF5F5F5; // Màu xám nhạt cho các trạng thái khác
        }
    }

    public int getStatusTextColor(String status) {
        if (status == null) {
            return 0xFF888888; // Màu xám đậm mặc định
        }
        switch (status) {
            case "confirmed":
                return 0xFF388E3C; // Màu xanh lá cây đậm
            case "pending":
                return 0xFFFFA000; // Màu cam đậm
            case "completed":
                return 0xFF0288D1; // Màu xanh dương đậm
            case "cancelled":
            case "rejected":
                return 0xFFD32F2F; // Màu đỏ đậm
            default:
                return 0xFF888888; // Màu xám đậm cho các trạng thái khác
        }
    }

    private void extractEventDays(ApiService.BookingsByWeekResponse response) {
        if (response == null || response.bookingsByDate == null) {
            eventDays.postValue(new ArrayList<>());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<Date> datesWithEvents = new ArrayList<>();

        for (String dateStr : response.bookingsByDate.keySet()) {
            try {
                List<Booking> bookingsOnDate = response.bookingsByDate.get(dateStr);
                if (bookingsOnDate != null && !bookingsOnDate.isEmpty()) {
                    datesWithEvents.add(sdf.parse(dateStr));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + dateStr, e);
            }
        }
        eventDays.postValue(datesWithEvents);
        Log.d(TAG, "Extracted " + datesWithEvents.size() + " event days.");
    }
}