package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.Teacher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacultyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy thông tin giảng viên từ Intent
        Intent intent = getIntent();
        String facultyUserId = intent.getStringExtra("facultyUserId");
        String facultyName = intent.getStringExtra("facultyName");
        String degree = intent.getStringExtra("degree");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String officeLocation = intent.getStringExtra("officeLocation");
        String departmentName = intent.getStringExtra("departmentName");
        String avatarUrl = intent.getStringExtra("avatarUrl");
        String email = intent.getStringExtra("email");

        // Ánh xạ các view
        ImageView backButton = findViewById(R.id.backButton);
        ImageView teacherBackgroundImageView = findViewById(R.id.teacherBackgroundImageView);
        TextView teacherName = findViewById(R.id.teacherName);
        TextView teacherDepartment = findViewById(R.id.teacherDepartment);
        TextView detailDepartment = findViewById(R.id.detailDepartment);
        TextView detailDegree = findViewById(R.id.detailDegree);
        TextView detailPhone = findViewById(R.id.detailPhone);
        TextView detailEmail = findViewById(R.id.detailEmail);
        TextView detailOffice = findViewById(R.id.detailOffice);
        Button bookAppointmentButton = findViewById(R.id.bookAppointmentButton);

        // Nếu có thông tin từ Intent thì hiển thị tạm
        String displayName = facultyName;
        if (degree != null && !degree.isEmpty()) {
            displayName = degree + ". " + facultyName;
        }
        teacherName.setText(displayName != null ? displayName : "");
        teacherDepartment.setText(departmentName != null ? ("Bộ môn " + departmentName) : "");
        detailDepartment.setText(departmentName != null ? departmentName : "");
        detailDegree.setText(degree != null ? degree : "");
        detailPhone.setText(phoneNumber != null ? phoneNumber : "");
        detailEmail.setText(email != null ? email : "");
        detailOffice.setText(officeLocation != null ? officeLocation : "");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.teacher_placeholder)
                .error(R.drawable.teacher_placeholder)
                .into(teacherBackgroundImageView);
        }

        // Gọi API lấy chi tiết giảng viên nếu có facultyUserId
        if (facultyUserId != null && !facultyUserId.isEmpty()) {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getTeacherDetail(facultyUserId).enqueue(new Callback<Teacher>() {
                @Override
                public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Teacher teacher = response.body();
                        String displayName = teacher.getDegree() != null && !teacher.getDegree().isEmpty()
                                ? teacher.getDegree() + ". " + teacher.getFacultyName()
                                : teacher.getFacultyName();
                        teacherName.setText(displayName != null ? displayName : "");
                        teacherDepartment.setText(teacher.getDepartmentName() != null ? ("Bộ môn " + teacher.getDepartmentName()) : "");
                        detailDepartment.setText(teacher.getDepartmentName() != null ? teacher.getDepartmentName() : "");
                        detailDegree.setText(teacher.getDegree() != null ? teacher.getDegree() : "");
                        detailPhone.setText(teacher.getPhoneNumber() != null ? teacher.getPhoneNumber() : "");
                        detailEmail.setText(teacher.getEmail() != null ? teacher.getEmail() : "");
                        detailOffice.setText(teacher.getOfficeLocation() != null ? teacher.getOfficeLocation() : "");
                        if (teacher.getAvatarUrl() != null && !teacher.getAvatarUrl().isEmpty()) {
                            Glide.with(FacultyDetailActivity.this)
                                .load(teacher.getAvatarUrl())
                                .placeholder(R.drawable.teacher_placeholder)
                                .error(R.drawable.teacher_placeholder)
                                .into(teacherBackgroundImageView);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Teacher> call, Throwable t) {
                    // Có thể hiển thị thông báo lỗi nếu muốn
                }
            });
        }

        // Xử lý nút back
        backButton.setOnClickListener(v -> finish());

        // Xử lý nút đặt lịch hẹn
        bookAppointmentButton.setOnClickListener(v -> {
            Intent bookIntent = new Intent(this, BookAppointmentActivity.class);
            bookIntent.putExtra("facultyUserId", facultyUserId);
            bookIntent.putExtra("facultyName", teacherName.getText().toString());
            bookIntent.putExtra("officeLocation", detailOffice.getText().toString());
            bookIntent.putExtra("email", detailEmail.getText().toString());
            startActivity(bookIntent);
        });
    }
}