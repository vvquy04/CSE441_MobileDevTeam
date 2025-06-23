package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tluofficehours.R;
import com.example.tluofficehours.FacultyDetailActivity;
import com.example.tluofficehours.adapter.TeacherAdapter;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.model.Teacher;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeachListActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener {
    private TeacherAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teach_list);
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //     Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //     return insets;
        // });

        String departmentId = getIntent().getStringExtra("departmentId");
        String departmentName = getIntent().getStringExtra("departmentName");
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Danh sách giảng viên");

        RecyclerView teacherRecyclerView = findViewById(R.id.teacherRecyclerView);
        adapter = new TeacherAdapter(this, new ArrayList<>(), this);
        teacherRecyclerView.setAdapter(adapter);
        teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Gọi API lấy danh sách giảng viên theo bộ môn
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getTeachersByDepartment(departmentId).enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateTeachers(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    public void onTeacherClick(Teacher teacher) {
        // Chuyển đến FacultyDetailActivity với thông tin giảng viên
        Intent intent = new Intent(this, FacultyDetailActivity.class);
        intent.putExtra("facultyUserId", teacher.getFacultyUserId());
        intent.putExtra("facultyName", teacher.getFacultyName());
        intent.putExtra("degree", teacher.getDegree());
        intent.putExtra("phoneNumber", teacher.getPhoneNumber());
        intent.putExtra("officeLocation", teacher.getOfficeLocation());
        intent.putExtra("departmentName", teacher.getDepartmentName());
        intent.putExtra("avatarUrl", teacher.getAvatarUrl());
        intent.putExtra("email", teacher.getEmail());
        startActivity(intent);
    }
}