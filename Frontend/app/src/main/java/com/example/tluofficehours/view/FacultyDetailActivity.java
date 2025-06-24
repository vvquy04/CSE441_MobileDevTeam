package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.viewmodel.TeacherViewModel;

public class FacultyDetailActivity extends AppCompatActivity {

    private TeacherViewModel viewModel;
    private TextView teacherName, teacherDepartment, detailDepartment, detailDegree, detailPhone, detailEmail, detailOffice;
    private ImageView teacherBackgroundImageView;
    private String facultyUserId;

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
        facultyUserId = intent.getStringExtra("facultyUserId");
        String facultyName = intent.getStringExtra("facultyName");
        String degree = intent.getStringExtra("degree");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String officeLocation = intent.getStringExtra("officeLocation");
        String departmentId = intent.getStringExtra("departmentId");
        String avatar = intent.getStringExtra("avatar");

        // Ánh xạ các view
        ImageView backButton = findViewById(R.id.backButton);
        teacherBackgroundImageView = findViewById(R.id.teacherBackgroundImageView);
        teacherName = findViewById(R.id.teacherName);
        teacherDepartment = findViewById(R.id.teacherDepartment);
        detailDepartment = findViewById(R.id.detailDepartment);
        detailDegree = findViewById(R.id.detailDegree);
        detailPhone = findViewById(R.id.detailPhone);
        detailEmail = findViewById(R.id.detailEmail);
        detailOffice = findViewById(R.id.detailOffice);
        Button bookAppointmentButton = findViewById(R.id.bookAppointmentButton);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TeacherViewModel.class);
        
        // Observe data changes
        observeViewModel();

        // Nếu có thông tin từ Intent thì hiển thị tạm
        String displayName = facultyName;
        if (degree != null && !degree.isEmpty()) {
            displayName = degree + ". " + facultyName;
        }
        teacherName.setText(displayName != null ? displayName : "");
        teacherDepartment.setText(departmentId != null ? ("Bộ môn " + departmentId) : "");
        detailDepartment.setText(departmentId != null ? departmentId : "");
        detailDegree.setText(degree != null ? degree : "");
        detailPhone.setText(phoneNumber != null ? phoneNumber : "");
        detailEmail.setText(""); // FacultyProfile không có email field
        detailOffice.setText(officeLocation != null ? officeLocation : "");
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.teacher_placeholder_img)
                .error(R.drawable.teacher_placeholder_img)
                .into(teacherBackgroundImageView);
        }

        // Load teacher details using ViewModel if facultyUserId is available
        if (facultyUserId != null && !facultyUserId.isEmpty()) {
            android.util.Log.d("FacultyDetailActivity", "Loading teacher detail for facultyUserId: " + facultyUserId);
            viewModel.loadTeacherDetail(facultyUserId);
        } else {
            android.util.Log.w("FacultyDetailActivity", "facultyUserId is null or empty");
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
    
    private void observeViewModel() {
        // Observe teacher details
        viewModel.getTeacherDetail().observe(this, teacher -> {
            if (teacher != null) {
                android.util.Log.d("FacultyDetailActivity", "Teacher detail loaded: " + teacher.getFacultyName());
                android.util.Log.d("FacultyDetailActivity", "Department name: " + teacher.getDepartmentName());
                android.util.Log.d("FacultyDetailActivity", "Email: " + teacher.getEmail());
                
                String displayName = teacher.getDegree() != null && !teacher.getDegree().isEmpty()
                        ? teacher.getDegree() + ". " + teacher.getFacultyName()
                        : teacher.getFacultyName();
                teacherName.setText(displayName != null ? displayName : "");
                
                // Sử dụng department_name thay vì department_id
                String deptText = teacher.getDepartmentName() != null ? ("Bộ môn " + teacher.getDepartmentName()) : "";
                teacherDepartment.setText(deptText);
                detailDepartment.setText(teacher.getDepartmentName() != null ? teacher.getDepartmentName() : "");
                
                detailDegree.setText(teacher.getDegree() != null ? teacher.getDegree() : "");
                detailPhone.setText(teacher.getPhoneNumber() != null ? teacher.getPhoneNumber() : "");
                detailEmail.setText(teacher.getEmail() != null ? teacher.getEmail() : "");
                detailOffice.setText(teacher.getOfficeLocation() != null ? teacher.getOfficeLocation() : "");
                
                String avatarUrl = teacher.getAvatarUrl();
                if (avatarUrl == null || avatarUrl.isEmpty()) {
                    avatarUrl = teacher.getAvatar();
                }
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.teacher_placeholder_img)
                        .error(R.drawable.teacher_placeholder_img)
                        .into(teacherBackgroundImageView);
                }
            } else {
                android.util.Log.w("FacultyDetailActivity", "Teacher detail is null");
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
    }
}