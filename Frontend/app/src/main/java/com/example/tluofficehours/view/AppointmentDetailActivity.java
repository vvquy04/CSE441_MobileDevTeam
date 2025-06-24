package com.example.tluofficehours.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.viewmodel.AppointmentViewModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView closeButton;
    private CircleImageView teacherAvatar;
    private TextView teacherName;
    private TextView teacherDepartment;
    private TextView appointmentDate;
    private TextView appointmentTime;
    private TextView appointmentPurpose;
    private TextView appointmentRoom;
    private TextView cancellationReasonText;
    private Button cancelAppointmentButton;
    private AppointmentViewModel viewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Ánh xạ các View
        closeButton = findViewById(R.id.closeButton);
        teacherAvatar = findViewById(R.id.teacherAvatar);
        teacherName = findViewById(R.id.teacherName);
        teacherDepartment = findViewById(R.id.teacherDepartment);
        appointmentDate = findViewById(R.id.appointmentDate);
        appointmentTime = findViewById(R.id.appointmentTime);
        appointmentPurpose = findViewById(R.id.appointmentPurpose);
        appointmentRoom = findViewById(R.id.appointmentRoom);
        cancellationReasonText = findViewById(R.id.cancellationReason);
        cancelAppointmentButton = findViewById(R.id.cancelAppointmentButton);

        // Nhận dữ liệu từ Intent và hiển thị
        loadAppointmentData();

        // Observe ViewModel data
        observeViewModel();

        // Xử lý sự kiện cho nút Đóng (X)
        closeButton.setOnClickListener(v -> {
            finish();
        });

        // Xử lý sự kiện cho nút Hủy lịch hẹn
        cancelAppointmentButton.setOnClickListener(v -> showCancelDialog());
    }

    private void loadAppointmentData() {
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String teacherNameStr = intent.getStringExtra("teacherName");
            String departmentStr = intent.getStringExtra("department");
            String avatarUrl = intent.getStringExtra("avatarUrl");
            String dateStr = intent.getStringExtra("date");
            String timeStr = intent.getStringExtra("time");
            String purposeStr = intent.getStringExtra("purpose");
            String roomStr = intent.getStringExtra("room");
            String status = intent.getStringExtra("status");
            String cancellationReason = intent.getStringExtra("cancellationReason");

            // Log dữ liệu nhận được
            android.util.Log.d("AppointmentDetail", "Received data:");
            android.util.Log.d("AppointmentDetail", "teacherName: " + teacherNameStr);
            android.util.Log.d("AppointmentDetail", "department: " + departmentStr);
            android.util.Log.d("AppointmentDetail", "avatarUrl: " + avatarUrl);
            android.util.Log.d("AppointmentDetail", "date: " + dateStr);
            android.util.Log.d("AppointmentDetail", "time: " + timeStr);
            android.util.Log.d("AppointmentDetail", "purpose: " + purposeStr);
            android.util.Log.d("AppointmentDetail", "room: " + roomStr);
            android.util.Log.d("AppointmentDetail", "status: " + status);
            android.util.Log.d("AppointmentDetail", "cancellationReason: " + cancellationReason);

            // Hiển thị dữ liệu với null safety
            teacherName.setText(teacherNameStr != null ? teacherNameStr : "Không có thông tin");
            teacherDepartment.setText(departmentStr != null ? departmentStr : "Không có thông tin");
            appointmentDate.setText(dateStr != null ? dateStr : "Không có thông tin");
            appointmentTime.setText(timeStr != null ? timeStr : "Không có thông tin");
            appointmentPurpose.setText(purposeStr != null ? purposeStr : "Không có mục đích");
            appointmentRoom.setText(roomStr != null ? roomStr : "Không có thông tin");
            
            // Log dữ liệu đã set vào TextView
            android.util.Log.d("AppointmentDetail", "Set to TextView:");
            android.util.Log.d("AppointmentDetail", "teacherName TextView: " + teacherName.getText());
            android.util.Log.d("AppointmentDetail", "teacherDepartment TextView: " + teacherDepartment.getText());
            
            // Hiển thị avatar
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.teacher_placeholder)
                    .error(R.drawable.teacher_placeholder)
                    .into(teacherAvatar);
            } else {
                teacherAvatar.setImageResource(R.drawable.teacher_placeholder);
            }

            // Hiển thị lý do hủy nếu có
            if ("CANCELLED".equalsIgnoreCase(status) && cancellationReason != null && !cancellationReason.isEmpty()) {
                if (cancellationReasonText != null) {
                    cancellationReasonText.setVisibility(View.VISIBLE);
                    cancellationReasonText.setText("Lý do hủy: " + cancellationReason);
                }
            } else if (cancellationReasonText != null) {
                cancellationReasonText.setVisibility(View.GONE);
            }

            // Ẩn nút hủy nếu đã hủy hoặc đã hoàn thành
            if ("CANCELLED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                cancelAppointmentButton.setVisibility(View.GONE);
            } else {
                cancelAppointmentButton.setVisibility(View.VISIBLE);
            }
        } else {
            // Fallback nếu không có intent
            android.util.Log.w("AppointmentDetail", "No intent received");
            teacherName.setText("Không có thông tin");
            teacherDepartment.setText("Không có thông tin");
            appointmentDate.setText("Không có thông tin");
            appointmentTime.setText("Không có thông tin");
            appointmentPurpose.setText("Không có mục đích");
            appointmentRoom.setText("Không có thông tin");
            teacherAvatar.setImageResource(R.drawable.teacher_placeholder);
        }
    }

    private void observeViewModel() {
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                cancelAppointmentButton.setEnabled(false);
                cancelAppointmentButton.setText("Đang hủy...");
            } else {
                cancelAppointmentButton.setEnabled(true);
                cancelAppointmentButton.setText("Hủy lịch hẹn");
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe cancel success
        viewModel.getCancelSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void showCancelDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Xác nhận hủy lịch hẹn của bạn?");
        final android.widget.EditText reasonEdit = new android.widget.EditText(this);
        reasonEdit.setHint("Lý do hủy");
        reasonEdit.setPadding(32, 32, 32, 32);
        builder.setView(reasonEdit);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String reason = reasonEdit.getText().toString().trim();
            if (reason.isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng nhập lý do hủy!", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                // Gọi ViewModel để hủy lịch
                cancelAppointment(reason);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_900));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
    }

    private void cancelAppointment(String reason) {
        int appointmentId = getIntent().getIntExtra("appointmentId", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Không tìm thấy mã lịch hẹn!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Gọi ViewModel thay vì API trực tiếp
        viewModel.cancelAppointment(appointmentId, reason);
    }
}