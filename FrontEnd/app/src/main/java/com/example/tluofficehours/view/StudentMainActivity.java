package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.view.FacultyDetailActivity;
import com.example.tluofficehours.adapter.TeacherAdapter;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.model.StudentProfile;
import com.example.tluofficehours.utils.SharedPrefsManager;
import com.example.tluofficehours.viewmodel.TeacherViewModel;
import com.example.tluofficehours.repository.StudentProfileRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.EditText;

public class StudentMainActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener {

    private TeacherViewModel teacherViewModel;
    private StudentProfileRepository studentProfileRepository;
    private TextView userNameText;
    private CircleImageView profileImage;
    private SharedPrefsManager sharedPrefsManager;
    private RecyclerView featuredTeachersRecyclerView;
    private TeacherAdapter teacherAdapter;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout paginationDots;
    private int currentDot = 0;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private List<Department> departmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_main);
        
        // Initialize SharedPrefsManager
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
        
        // Set auth token for API calls
        String token = sharedPrefsManager.getAuthToken();
        android.util.Log.d("StudentMainActivity", "Token from SharedPrefs: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        if (token != null && !token.isEmpty()) {
            com.example.tluofficehours.api.RetrofitClient.setAuthToken(token);
            android.util.Log.d("StudentMainActivity", "Token set to RetrofitClient");
        } else {
            android.util.Log.w("StudentMainActivity", "No token found in SharedPrefs");
        }
        
        // Initialize views
        userNameText = findViewById(R.id.userNameText);
        profileImage = findViewById(R.id.profileImage);
        featuredTeachersRecyclerView = findViewById(R.id.featuredTeachersRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        paginationDots = findViewById(R.id.paginationDots);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup Bottom Navigation
        setupBottomNavigation();
        
        // Initialize ViewModels and Repositories
        teacherViewModel = new ViewModelProvider(this).get(TeacherViewModel.class);
        studentProfileRepository = new StudentProfileRepository();
        
        // Observe data changes
        observeViewModels();
        
        // Load data
        loadUserProfile();
        teacherViewModel.loadDepartments();
        teacherViewModel.loadFeaturedTeachers();
        
        // Set up window insets for the root layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Lắng nghe sự kiện scroll của RecyclerView để cập nhật dot active
        featuredTeachersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (firstVisible != currentDot) {
                    currentDot = firstVisible;
                    updatePaginationDots(teacherAdapter.getItemCount());
                }
            }
        });

        // Thêm logic tìm kiếm giảng viên khi nhập vào EditText
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> teacherViewModel.searchTeachers(s.toString());
                searchHandler.postDelayed(searchRunnable, 400); // debounce 400ms
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý click vào từng bộ môn để mở danh sách giảng viên theo bộ môn
        findViewById(R.id.cardCNPM).setOnClickListener(v -> {
            String id = getDepartmentIdByName("Công nghệ phần mềm");
            if (id != null) openTeacherListActivity(id, "Công nghệ phần mềm");
        });
        findViewById(R.id.cardHTTT).setOnClickListener(v -> {
            String id = getDepartmentIdByName("Hệ thống thông tin");
            if (id != null) openTeacherListActivity(id, "Hệ thống thông tin");
        });
        findViewById(R.id.cardANM).setOnClickListener(v -> {
            String id = getDepartmentIdByName("Mạng và an toàn bảo mật");
            if (id != null) openTeacherListActivity(id, "Mạng và an toàn bảo mật");
        });
        findViewById(R.id.cardTTNT).setOnClickListener(v -> {
            String id = getDepartmentIdByName("Trí tuệ nhân tạo");
            if (id != null) openTeacherListActivity(id, "Trí tuệ nhân tạo");
        });
    }
    
    private void setupRecyclerView() {
        teacherAdapter = new TeacherAdapter(this, new ArrayList<>(), this);
        featuredTeachersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        featuredTeachersRecyclerView.setAdapter(teacherAdapter);
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Đã ở trang chủ, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                // Điều hướng sang MyAppointmentActivity
                Intent intent = new Intent(this, com.example.tluofficehours.view.MyAppointmentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Điều hướng sang StudentProfileActivity
                Intent intent = new Intent(this, StudentProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });
        
        // Set home as selected by default
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
    
    private void loadUserProfile() {
        userNameText.setText("Đang tải...");
        
        studentProfileRepository.getStudentProfile(new StudentProfileRepository.StudentProfileCallback() {
            @Override
            public void onSuccess(StudentProfile studentProfile) {
                if (studentProfile.getProfile() != null) {
                    String name = studentProfile.getProfile().getStudentName();
                    if (name != null && !name.isEmpty()) {
                        userNameText.setText(name);
                        android.util.Log.d("StudentMainActivity", "Set user name: " + name);
                    } else {
                        if (studentProfile.getUser() != null && studentProfile.getUser().getEmail() != null) {
                            String email = studentProfile.getUser().getEmail();
                            userNameText.setText(email);
                            android.util.Log.w("StudentMainActivity", "Using email as fallback: " + email);
                        } else {
                            android.util.Log.w("StudentMainActivity", "No name or email available");
                            userNameText.setText("Sinh viên");
                        }
                    }
                } else {
                    android.util.Log.w("StudentMainActivity", "Profile object is null");
                    userNameText.setText("Sinh viên");
                }
                
                if (studentProfile.getAvatarUrl() != null && !studentProfile.getAvatarUrl().isEmpty()) {
                    Glide.with(StudentMainActivity.this)
                        .load(studentProfile.getAvatarUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(profileImage);
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("StudentMainActivity", "Error loading profile: " + message);
                userNameText.setText("Sinh viên");
                Toast.makeText(StudentMainActivity.this, "Lỗi tải thông tin: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void observeViewModels() {
        // Observe teacher data (TeacherViewModel)
        teacherViewModel.getFeaturedTeachers().observe(this, teachers -> {
            android.util.Log.d("StudentMainActivity", "Featured teachers loaded: " + (teachers != null ? teachers.size() : "null"));
            if (teachers != null) {
                // Nếu không có search, hiển thị featured teachers
                if (isSearchEmpty()) {
                    teacherAdapter.updateTeachers(teachers);
                    currentDot = 0;
                    updatePaginationDots(teachers.size());
                }
            } else {
                updatePaginationDots(0);
            }
        });
        // Observe teachers list (kết quả tìm kiếm)
        teacherViewModel.getTeachers().observe(this, teachers -> {
            android.util.Log.d("StudentMainActivity", "Search teachers loaded: " + (teachers != null ? teachers.size() : "null"));
            if (teachers != null && !isSearchEmpty()) {
                teacherAdapter.updateTeachers(teachers);
                currentDot = 0;
                updatePaginationDots(teachers.size());
            }
        });
        teacherViewModel.getDepartments().observe(this, departments -> {
            android.util.Log.d("StudentMainActivity", "Departments loaded: " + (departments != null ? departments.size() : "null"));
            if (departments != null) {
                departmentList.clear();
                departmentList.addAll(departments);
            }
        });
        teacherViewModel.getIsLoading().observe(this, isLoading -> {
            // Optionally show loading indicator
        });
        teacherViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTeacherClick(FacultyProfile teacher) {
        // Chỉ truyền facultyUserId sang FacultyDetailActivity
        Intent intent = new Intent(this, FacultyDetailActivity.class);
        intent.putExtra("facultyUserId", teacher.getFacultyUserId());
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload user profile data when returning to this activity
        loadUserProfile();
    }

    // Hàm cập nhật các dot bên dưới RecyclerView
    private void updatePaginationDots(int count) {
        paginationDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            int size = (int) getResources().getDimension(R.dimen.dot_size); // tạo dimens nếu chưa có
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(i == currentDot ? R.drawable.dot_active : R.drawable.dot_inactive);
            paginationDots.addView(dot);
        }
    }

    private void openTeacherListActivity(String departmentId, String departmentName) {
        Intent intent = new Intent(this, TeachListActivity.class);
        intent.putExtra("departmentId", departmentId);
        intent.putExtra("departmentName", departmentName);
        startActivity(intent);
    }

    private String getDepartmentIdByName(String name) {
        for (Department dept : departmentList) {
            if (dept.getName().equalsIgnoreCase(name)) {
                return String.valueOf(dept.getDepartmentId());
            }
        }
        return null;
    }

    // Hàm kiểm tra ô tìm kiếm có rỗng không
    private boolean isSearchEmpty() {
        EditText searchEditText = findViewById(R.id.searchEditText);
        return searchEditText.getText().toString().trim().isEmpty();
    }
}