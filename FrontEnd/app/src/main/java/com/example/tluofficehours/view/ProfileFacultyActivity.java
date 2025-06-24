package com.example.tluofficehours.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.viewmodel.ProfileFacultyViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.tluofficehours.repository.AuthRepository;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.ArrayList;

public class ProfileFacultyActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private EditText edtFacultyName, edtDegree, edtPhone, edtOffice;
    private Spinner spinnerDepartment;
    private TextView facultyNameTextView, departmentTextView, phoneTextView, emailTextView, userNameTextView;
    private Button updateButton;
    private ProfileFacultyViewModel viewModel;
    private CircleImageView profileImageView;
    private ImageView logoutIcon;
    private Uri selectedAvatarUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private List<Department> departments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_faculty);
        
        initViews();
        setupViewModel();
        setupBottomNavigation();
        setupClickListeners();
        loadFacultyData();
        loadDepartments();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        edtFacultyName = findViewById(R.id.edtFacultyName);
        edtDegree = findViewById(R.id.edtDegree);
        edtPhone = findViewById(R.id.edtPhone);
        edtOffice = findViewById(R.id.edtOffice);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        updateButton = findViewById(R.id.updateButton);
        profileImageView = findViewById(R.id.profileImageView);
        logoutIcon = findViewById(R.id.topRightIcon);
        userNameTextView = findViewById(R.id.userNameTextView);
        logoutIcon.setOnClickListener(v -> {
            com.example.tluofficehours.utils.SharedPrefsManager.getInstance(this).clearUserData();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri;
                    Glide.with(this).load(uri).into(profileImageView);
                }
            }
        );
        profileImageView.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileFacultyViewModel.class);
        
        // Observe faculty profile data
        viewModel.getFacultyProfile().observe(this, this::updateUI);
        
        // Observe email
        viewModel.getEmail().observe(this, email -> {
            if (email != null && !email.isEmpty()) {
                emailTextView.setText(email);
            } else {
                emailTextView.setText("Chưa cập nhật");
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            updateButton.setEnabled(!isLoading);
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });
        
        // Observe success messages
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearSuccessMessage();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }

    private void setupClickListeners() {
        updateButton.setOnClickListener(v -> {
            String facultyName = edtFacultyName.getText().toString().trim();
            String degree = edtDegree.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String office = edtOffice.getText().toString().trim();
            Department selectedDepartment = (Department) spinnerDepartment.getSelectedItem();
            String departmentId = selectedDepartment != null ? selectedDepartment.getDepartmentId() : "";
            viewModel.updateFacultyProfile(this, facultyName, departmentId, degree, phone, office, selectedAvatarUri);
        });
    }

    private void loadFacultyData() {
        viewModel.loadFacultyProfile();
    }

    private void loadDepartments() {
        AuthRepository authRepository = new AuthRepository();
        authRepository.getDepartments().enqueue(new retrofit2.Callback<List<Department>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Department>> call, retrofit2.Response<List<Department>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departments = response.body();
                    ArrayAdapter<Department> adapter = new ArrayAdapter<>(ProfileFacultyActivity.this, android.R.layout.simple_spinner_item, departments);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDepartment.setAdapter(adapter);
                    // Nếu đã có profile, set selection đúng bộ môn
                    FacultyProfile profile = viewModel.getFacultyProfile().getValue();
                    if (profile != null && profile.getDepartmentId() != null) {
                        for (int i = 0; i < departments.size(); i++) {
                            if (profile.getDepartmentId().equals(departments.get(i).getDepartmentId())) {
                                spinnerDepartment.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(ProfileFacultyActivity.this, "Không thể tải danh sách bộ môn", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Department>> call, Throwable t) {
                Toast.makeText(ProfileFacultyActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FacultyProfile profile) {
        if (profile != null) {
            // Luôn hiển thị đầy đủ các trường
            edtFacultyName.setText(profile.getFacultyName() != null ? profile.getFacultyName() : "");
            edtDegree.setText(profile.getDegree() != null ? profile.getDegree() : "");
            edtPhone.setText(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "");
            edtOffice.setText(profile.getOfficeLocation() != null ? profile.getOfficeLocation() : "");
            emailTextView.setText(profile.getEmail() != null ? profile.getEmail() : "");
            if (userNameTextView != null) {
                userNameTextView.setText(profile.getFacultyName() != null ? profile.getFacultyName() : "");
            }
            // Set spinner bộ môn
            if (departments != null && profile.getDepartmentId() != null) {
                for (int i = 0; i < departments.size(); i++) {
                    if (profile.getDepartmentId().equals(departments.get(i).getDepartmentId())) {
                        spinnerDepartment.setSelection(i);
                        break;
                    }
                }
            }
            // Set avatar
            if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.teacher_placeholder_img)
                    .error(R.drawable.teacher_placeholder_img)
                    .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.teacher_placeholder_img);
            }
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.navigation_home) {
            Intent intent = new Intent(this, FacultyMainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_calendar) {
            Intent intent = new Intent(this, FacultyCalendarActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_profile) {
            // Already on profile
            return true;
        }
        
        return false;
    }
} 