// BookAppointmentActivity.java
package com.example.tluofficehours.view; // Updated package

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tluofficehours.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.model.AvailableSlot;
import com.example.tluofficehours.viewmodel.AppointmentViewModel;
import com.google.android.material.button.MaterialButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private TextView currentMonthYear;
    private GridLayout calendarDaysGrid;
    private GridLayout timeSlotsGrid;
    private Button confirmButton;
    private Button cancelButton;

    private LocalDate selectedDate;
    private AvailableSlot selectedAvailableSlot;
    private String facultyUserId;
    private String facultyName;
    private String officeLocation;
    private String contactEmail;

    private YearMonth currentCalendarMonth;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter monthYearFormatter;

    private AppointmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Đảm bảo luôn set lại token cho RetrofitClient
        com.example.tluofficehours.utils.SharedPrefsManager sharedPrefsManager = com.example.tluofficehours.utils.SharedPrefsManager.getInstance(this);
        String token = sharedPrefsManager.getAuthToken();
        android.util.Log.d("BookAppointment", "Token from SharedPrefs: " + token);
        if (token != null && !token.isEmpty()) {
            com.example.tluofficehours.api.RetrofitClient.setAuthToken(token);
            android.util.Log.d("BookAppointment", "Token set to RetrofitClient: " + token.substring(0, Math.min(20, token.length())) + "...");
        } else {
            android.util.Log.w("BookAppointment", "No token found in SharedPrefs");
        }

        // Lấy thông tin giảng viên từ Intent
        Intent intent = getIntent();
        facultyUserId = intent.getStringExtra("facultyUserId");
        facultyName = intent.getStringExtra("facultyName");
        officeLocation = intent.getStringExtra("officeLocation");
        contactEmail = intent.getStringExtra("email");

        // Ánh xạ View
        ImageView prevMonthButton = findViewById(R.id.prevMonthButton);
        ImageView nextMonthButton = findViewById(R.id.nextMonthButton);
        currentMonthYear = findViewById(R.id.currentMonthYear);
        calendarDaysGrid = findViewById(R.id.calendarDaysGrid);
        timeSlotsGrid = findViewById(R.id.timeSlotsGrid);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Khởi tạo định dạng ngày/tháng
        dateFormatter = DateTimeFormatter.ofPattern("d");
        // Đảm bảo Locale tiếng Việt cho tháng năm
        monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("vi", "VN"));
        currentCalendarMonth = YearMonth.now(); // Bắt đầu từ tháng hiện tại

        // Gán selectedDate mặc định là ngày hôm nay
        selectedDate = LocalDate.now();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        
        // Observe data changes
        observeViewModel();
        
        // Xử lý chuyển tháng
        prevMonthButton.setOnClickListener(v -> {
            currentCalendarMonth = currentCalendarMonth.minusMonths(1);
            displayCalendar();
            clearTimeSlots(); // Xóa slot khi chuyển tháng
        });
        nextMonthButton.setOnClickListener(v -> {
            currentCalendarMonth = currentCalendarMonth.plusMonths(1);
            displayCalendar();
            clearTimeSlots(); // Xóa slot khi chuyển tháng
        });

        // Xử lý nút Xác nhận
        confirmButton.setOnClickListener(v -> {
            if (selectedDate != null && selectedAvailableSlot != null) {
                Map<String, Object> bookingData = new HashMap<>();
                bookingData.put("SlotId", selectedAvailableSlot.getSlotId());
                bookingData.put("Purpose", "Đặt lịch hẹn"); // Có thể lấy từ EditText nếu muốn

                viewModel.bookAppointment(bookingData);
            } else {
                Toast.makeText(this, "Vui lòng chọn ngày và giờ đặt lịch.", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Quay lại
        cancelButton.setOnClickListener(v -> finish()); // Đóng Activity

        // Hiển thị lịch ban đầu
        displayCalendar();
        // Gọi API lấy slot cho ngày hôm nay
        fetchAvailableSlotsForDate(selectedDate);
    }

    private void observeViewModel() {
        // Observe available slots
        viewModel.getAvailableSlots().observe(this, slots -> {
            if (slots != null) {
                displayTimeSlotsFromApi(slots);
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Có thể hiển thị loading indicator nếu cần
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe booking success
        viewModel.getBookingSuccess().observe(this, success -> {
            if (success) {
                Intent intent = new Intent(BookAppointmentActivity.this, BookingSuccessActivity.class);
                // Truyền thông tin cần thiết qua intent
                intent.putExtra("teacherName", facultyName);
                DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = selectedDate.format(outputDateFormatter);
                intent.putExtra("date", formattedDate);
                // Sử dụng method getTimeDisplay() để lấy chuỗi thời gian
                intent.putExtra("time", selectedAvailableSlot.getTimeDisplay());
                intent.putExtra("room", officeLocation);
                intent.putExtra("contactEmail", contactEmail);
                intent.putExtra("memberCount", 1); // Assuming memberCount is 1 for now
                intent.putExtra("purpose", "Đặt lịch hẹn");
                startActivity(intent);
                finish();
            }
        });
    }

    private void displayCalendar() {
        currentMonthYear.setText(monthYearFormatter.format(currentCalendarMonth));
        calendarDaysGrid.removeAllViews();

        // Xác định ngày đầu tiên của tháng và ngày trong tuần của nó
        LocalDate firstDayOfMonth = currentCalendarMonth.atDay(1);
        DayOfWeek dayOfWeekOfFirstDay = firstDayOfMonth.getDayOfWeek();

        // Tìm vị trí của ngày đầu tiên trong tuần (Mon=1, Sun=7)
        // WeekFields.of(Locale.getDefault()).firstDayOfWeek để đảm bảo tuần bắt đầu đúng theo locale
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int offset = dayOfWeekOfFirstDay.getValue() - weekFields.getFirstDayOfWeek().getValue();
        if (offset < 0) { // Xử lý trường hợp tuần bắt đầu Chủ Nhật và dayOfWeek là Thứ Hai
            offset += 7;
        }

        // Thêm ô trống cho các ngày trước ngày đầu tiên của tháng
        for (int i = 0; i < offset; i++) {
            TextView emptyDayView = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyDayView.setLayoutParams(params);
            calendarDaysGrid.addView(emptyDayView);
        }

        // Thêm các ngày của tháng
        for (int dayOfMonth = 1; dayOfMonth <= currentCalendarMonth.lengthOfMonth(); dayOfMonth++) {
            LocalDate date = currentCalendarMonth.atDay(dayOfMonth);
            TextView dayView = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            // Đặt kích thước cố định để tạo hình tròn hoàn hảo
            int daySize = (int) (40 * getResources().getDisplayMetrics().density);
            params.height = daySize;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density),
                    0, (int) (8 * getResources().getDisplayMetrics().density));
            dayView.setLayoutParams(params);

            dayView.setText(dateFormatter.format(date));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(16); // sp
            dayView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            dayView.setTag("day_" + dayOfMonth); // Đặt tag để dễ dàng tìm lại view

            // Đánh dấu ngày hiện tại
            if (date.isEqual(LocalDate.now())) {
                dayView.setBackgroundResource(R.drawable.calendar_today_background);
            }
            // Đánh dấu ngày được chọn (kiểm tra null)
            if (selectedDate != null && date.isEqual(selectedDate)) {
                dayView.setBackgroundResource(R.drawable.calendar_selected_background);
                dayView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }

            dayView.setOnClickListener(v -> selectDate(date));
            calendarDaysGrid.addView(dayView);
        }
    }

    private void selectDate(@NonNull LocalDate date) {
        // Hủy chọn ngày cũ (nếu có)
        if (selectedDate != null) {
            View oldDayView = calendarDaysGrid.findViewWithTag("day_" + selectedDate.getDayOfMonth());
            if (oldDayView instanceof TextView) {
                TextView oldTextView = (TextView) oldDayView;
                // Đặt lại màu nền và chữ cho ngày cũ
                if (selectedDate.isEqual(LocalDate.now())) {
                    oldTextView.setBackgroundResource(R.drawable.calendar_today_background);
                } else {
                    oldTextView.setBackgroundResource(0); // Không có nền
                }
                oldTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }

        // Chọn ngày mới
        selectedDate = date;
        View newDayView = calendarDaysGrid.findViewWithTag("day_" + date.getDayOfMonth());
        if (newDayView instanceof TextView) {
            TextView newTextView = (TextView) newDayView;
            newTextView.setBackgroundResource(R.drawable.calendar_selected_background);
            newTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }

        fetchAvailableSlotsForDate(date);
    }

    private void clearTimeSlots() {
        // Reset trạng thái chọn của slot cũ
        if (selectedAvailableSlot != null && selectedAvailableSlot.getButtonView() != null) {
            selectedAvailableSlot.getButtonView().setSelected(false);
            selectedAvailableSlot.setSelected(false);
        }
        timeSlotsGrid.removeAllViews();
        selectedAvailableSlot = null;
    }

    private void fetchAvailableSlotsForDate(LocalDate date) {
        selectedDate = date;
        clearTimeSlots();
        String dateStr = date.toString(); // yyyy-MM-dd
        viewModel.fetchAvailableSlots(facultyUserId, dateStr);
    }

    private void displayTimeSlotsFromApi(List<AvailableSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            showNoSlotsMessage();
            return;
        }
        
        for (AvailableSlot slot : slots) {
            Button timeButton = new Button(this);
            // Sử dụng method getTimeDisplay() để lấy chuỗi thời gian
            timeButton.setText(slot.getTimeDisplay());
            timeButton.setTag(slot);
            
            // Kiểm tra trạng thái available
            if (slot.isAvailable()) {
                timeButton.setEnabled(true);
                timeButton.setOnClickListener(v -> {
                    // Hủy chọn slot cũ (nếu có)
                    if (selectedAvailableSlot != null && selectedAvailableSlot.getButtonView() != null) {
                        selectedAvailableSlot.getButtonView().setSelected(false);
                        selectedAvailableSlot.setSelected(false);
                    }
                    
                    // Chọn slot mới
                    selectedAvailableSlot = slot;
                    slot.setSelected(true);
                    slot.setButtonView(timeButton);
                    timeButton.setSelected(true);
                    
                    showBookingInfoDialog(slot);
                });
            } else {
                timeButton.setEnabled(false);
                timeButton.setOnClickListener(v -> showUnavailableDialog());
            }
            
            timeButton.setTextAppearance(R.style.TimeSlotButton);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            timeButton.setLayoutParams(params);
            slot.setButtonView(timeButton);
            timeSlotsGrid.addView(timeButton);
        }
    }

    private void showNoSlotsMessage() {
        TextView noSlotsText = new TextView(this);
        noSlotsText.setText("Không có khung giờ đặt lịch nào cho ngày này.");
        noSlotsText.setGravity(Gravity.CENTER);
        noSlotsText.setTextSize(16);
        noSlotsText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(0, 4);
        params.setMargins(0, (int) (16 * getResources().getDisplayMetrics().density), 0, 0);
        noSlotsText.setLayoutParams(params);
        timeSlotsGrid.addView(noSlotsText);
    }

    // Gọi API đặt lịch với thông tin nhập
    private void bookSlot(AvailableSlot slot, int memberCount, String reason) {
        java.util.Map<String, Object> bookingData = new java.util.HashMap<>();
        bookingData.put("SlotId", slot.getSlotId());
        bookingData.put("Purpose", reason);
        if (slot.getMaxStudents() > 1) {
            bookingData.put("MemberCount", memberCount);
        }
        viewModel.bookAppointment(bookingData);
    }

    // Hiển thị dialog nhập thông tin đặt lịch
    private void showBookingInfoDialog(AvailableSlot slot) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nhập thông tin đặt lịch");
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        android.widget.EditText memberCountEdit = null;
        if (slot.getMaxStudents() > 1) {
            memberCountEdit = new android.widget.EditText(this);
            memberCountEdit.setHint("Số lượng sinh viên");
            memberCountEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            layout.addView(memberCountEdit);
        }
        android.widget.EditText reasonEdit = new android.widget.EditText(this);
        reasonEdit.setHint("Lý do hẹn gặp");
        layout.addView(reasonEdit);

        builder.setView(layout);
        android.widget.EditText finalMemberCountEdit = memberCountEdit;
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String reason = reasonEdit.getText().toString().trim();
            int memberCount = 1;
            if (finalMemberCountEdit != null) {
                String countStr = finalMemberCountEdit.getText().toString().trim();
                if (!countStr.isEmpty()) {
                    memberCount = Integer.parseInt(countStr);
                }
            }
            bookSlot(slot, memberCount, reason);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Dialog: slot đã có sinh viên đặt
    private void showBookedDialog() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Thông báo")
            .setMessage("Khung giờ này đã có sinh viên đặt. Vui lòng chọn thời gian khác.")
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }

    // Dialog: slot giảng viên bận
    private void showUnavailableDialog() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Thông báo")
            .setMessage("Giảng viên bận vào thời gian này. Vui lòng chọn thời gian khác.")
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }
}