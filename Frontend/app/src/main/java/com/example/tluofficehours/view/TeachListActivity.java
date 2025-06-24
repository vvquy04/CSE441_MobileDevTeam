package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tluofficehours.R;
import com.example.tluofficehours.view.FacultyDetailActivity;
import com.example.tluofficehours.adapter.TeacherAdapter;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.viewmodel.TeacherViewModel;
import java.util.ArrayList;
import java.util.List;

public class TeachListActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener {
    private TeacherAdapter adapter;
    private TeacherViewModel viewModel;
    
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

        // Initialize ViewModel first
        viewModel = new ViewModelProvider(this).get(TeacherViewModel.class);

        RecyclerView teacherRecyclerView = findViewById(R.id.teacherRecyclerView);
        adapter = new TeacherAdapter(this, new ArrayList<>(), this);
        teacherRecyclerView.setAdapter(adapter);
        teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        
        // Observe data changes
        observeViewModel();
        
        // Load departments first, then load teachers
        viewModel.loadDepartments();
        viewModel.loadTeachersByDepartment(departmentId);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        // Observe teachers list
        viewModel.getTeachers().observe(this, teachers -> {
            if (teachers != null) {
                adapter.updateTeachers(teachers);
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

    @Override
    public void onTeacherClick(FacultyProfile teacher) {
        // Chuyển đến FacultyDetailActivity với thông tin giảng viên
        Intent intent = new Intent(this, FacultyDetailActivity.class);
        intent.putExtra("facultyUserId", teacher.getFacultyUserId());
        intent.putExtra("facultyName", teacher.getFacultyName());
        intent.putExtra("degree", teacher.getDegree());
        intent.putExtra("phoneNumber", teacher.getPhoneNumber());
        intent.putExtra("officeLocation", teacher.getOfficeLocation());
        intent.putExtra("departmentId", teacher.getDepartmentId());
        intent.putExtra("avatar", teacher.getAvatar());
        startActivity(intent);
    }
}