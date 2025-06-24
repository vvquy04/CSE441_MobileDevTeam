package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.AddScheduleData;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CreateScheduleSuccessActivity extends AppCompatActivity {

    private static final String TAG = "CreateScheduleSuccess";
    private TextView tvScheduleType, tvApplicableDate, tvTotalSlots, tvNotes;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started.");
        setContentView(R.layout.activity_create_schedule_success);

        initViews();

        AddScheduleData scheduleData = (AddScheduleData) getIntent().getSerializableExtra("SCHEDULE_DATA");
        Log.d(TAG, "onCreate: Received schedule data: " + (scheduleData != null ? scheduleData.toString() : "null"));

        if (scheduleData != null) {
            populateData(scheduleData);
        } else {
            Log.w(TAG, "onCreate: No schedule data received!");
        }

        backToHomeButton.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Back to home button clicked.");
            Intent intent = new Intent(CreateScheduleSuccessActivity.this, FacultyMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        tvScheduleType = findViewById(R.id.tvScheduleType);
        tvApplicableDate = findViewById(R.id.tvApplicableDate);
        tvTotalSlots = findViewById(R.id.tvTotalSlots);
        tvNotes = findViewById(R.id.tvNotes);
        backToHomeButton = findViewById(R.id.backToHomeButton);
    }

    private void populateData(AddScheduleData data) {
        tvScheduleType.setText(data.isSpecificDateMode() ? "Ngày cụ thể" : "Lịch cố định");

        if (data.isSpecificDateMode() && data.getSelectedDate() != null) {
            try {
                // Chuyển String "yyyy-MM-dd" sang Date
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.util.Date date = inputFormat.parse(data.getSelectedDate());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvApplicableDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                tvApplicableDate.setText(data.getSelectedDate()); // fallback nếu lỗi
            }
        } else {
            // Đối với lịch cố định
            if (data.isApplyMonthly()) {
                // Tính số tuần trong tháng
                int totalWeeks = calculateTotalWeeksInMonth();
                String patternDays = getPatternDays(data);
                tvApplicableDate.setText("Hàng tuần theo lịch\n(" + totalWeeks + " tuần, Pattern: " + patternDays + ")");
            } else {
                String patternDays = getPatternDays(data);
                tvApplicableDate.setText("Hàng tuần theo lịch\n(Tuần hiện tại, Pattern: " + patternDays + ")");
            }
        }

        // Tính tổng số slots
        int totalSlots = 0;
        if (data.isSpecificDateMode()) {
            if (data.getTimeSlots() != null) {
                totalSlots = data.getTimeSlots().size();
            }
        } else {
            if (data.getDayTimeSlots() != null) {
                for (AddScheduleData.DayTimeSlot dayTimeSlot : data.getDayTimeSlots()) {
                    totalSlots += dayTimeSlot.getTimeSlots().size();
                }
            }
        }

        if (data.isApplyMonthly() && !data.isSpecificDateMode()) {
            int totalWeeks = calculateTotalWeeksInMonth();
            tvTotalSlots.setText(String.valueOf(totalSlots * totalWeeks));
        } else {
            tvTotalSlots.setText(String.valueOf(totalSlots));
        }

        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            tvNotes.setText(data.getNotes());
        } else {
            tvNotes.setText("Không có ghi chú");
        }
    }

    private int calculateTotalWeeksInMonth() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentWeek = calendar.get(java.util.Calendar.WEEK_OF_MONTH);
        int totalWeeksInMonth = calendar.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH);
        return totalWeeksInMonth - currentWeek + 1;
    }

    private String getPatternDays(AddScheduleData data) {
        if (data.getDayTimeSlots() == null || data.getDayTimeSlots().isEmpty()) {
            return "Không có ngày nào được chọn";
        }

        String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        StringBuilder pattern = new StringBuilder();
        
        for (int i = 0; i < data.getDayTimeSlots().size(); i++) {
            if (i > 0) pattern.append(", ");
            pattern.append(dayNames[data.getDayTimeSlots().get(i).getDayOfWeek() - 1]);
        }
        
        return pattern.toString();
    }
} 