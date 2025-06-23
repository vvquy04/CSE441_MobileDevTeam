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
import com.example.tluofficehours.FacultyDetailActivity;
import com.example.tluofficehours.adapter.TeacherAdapter;
import com.example.tluofficehours.model.Teacher;
import com.example.tluofficehours.utils.SharedPrefsManager;
import com.example.tluofficehours.viewmodel.StudentMainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.EditText;
import com.example.tluofficehours.model.Department;

public class StudentMainActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener {

    private StudentMainViewModel viewModel;
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
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StudentMainViewModel.class);
        
        // Observe data changes
        observeViewModel();
        
        // Load data
        viewModel.loadUserProfile();
        viewModel.loadFeaturedTeachers();
        
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
        android.widget.EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> searchTeachersApi(s.toString());
                searchHandler.postDelayed(searchRunnable, 400); // debounce 400ms
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Gọi API lấy danh sách bộ môn
        loadDepartments();

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
                Intent intent = new Intent(this, com.example.tluofficehours.MyAppointmentActivity.class);
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
    
    private void observeViewModel() {
        // Observe user name
        viewModel.getUserName().observe(this, name -> {
            if (name != null && !name.isEmpty()) {
                userNameText.setText(name);
            } else {
                userNameText.setText("Đang tải...");
            }
        });
        
        // Observe avatar URL
        viewModel.getAvatarUrl().observe(this, avatarUrl -> {
            android.util.Log.d("StudentMainActivity", "Avatar URL received: " + avatarUrl);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                android.util.Log.d("StudentMainActivity", "Loading avatar with Glide: " + avatarUrl);
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            android.util.Log.e("StudentMainActivity", "Avatar load failed: " + e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("StudentMainActivity", "Avatar loaded successfully");
                            return false;
                        }
                    })
                    .into(profileImage);
            } else {
                android.util.Log.w("StudentMainActivity", "Avatar URL is null or empty");
            }
        });
        
        // Observe featured teachers
        viewModel.getFeaturedTeachers().observe(this, teachers -> {
            android.util.Log.d("StudentMainActivity", "Featured teachers received: " + (teachers != null ? teachers.size() : "null"));
            if (teachers != null) {
                android.util.Log.d("StudentMainActivity", "Updating teacher adapter with " + teachers.size() + " teachers");
                teacherAdapter.updateTeachers(teachers);
                currentDot = 0;
                updatePaginationDots(teachers.size());
            } else {
                android.util.Log.w("StudentMainActivity", "Teachers list is null");
                updatePaginationDots(0);
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                userNameText.setText("Đang tải...");
            }
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTeacherClick(Teacher teacher) {
        // Chỉ truyền facultyUserId sang FacultyDetailActivity
        Intent intent = new Intent(this, FacultyDetailActivity.class);
        intent.putExtra("facultyUserId", teacher.getFacultyUserId());
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload user profile data when returning to this activity
        viewModel.loadUserProfile();
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

    // Hàm gọi API tìm kiếm giảng viên
    private void searchTeachersApi(String query) {
        if (query.isEmpty()) {
            viewModel.loadFeaturedTeachers();
            return;
        }
        ApiService apiService = RetrofitClient.getApiService();
        apiService.searchTeachers(query).enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teacherAdapter.updateTeachers(response.body());
                    currentDot = 0;
                    updatePaginationDots(response.body().size());
                }
            }
            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                // Có thể hiển thị thông báo lỗi nếu muốn
            }
        });
    }

    private void openTeacherListActivity(String departmentId, String departmentName) {
        Intent intent = new Intent(this, TeachListActivity.class);
        intent.putExtra("departmentId", departmentId);
        intent.putExtra("departmentName", departmentName);
        startActivity(intent);
    }

    private void loadDepartments() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getDepartments().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departmentList.clear();
                    departmentList.addAll(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private String getDepartmentIdByName(String name) {
        for (Department dept : departmentList) {
            if (dept.getName().equalsIgnoreCase(name)) {
                return String.valueOf(dept.getDepartmentId());
            }
        }
        return null;
    }
}