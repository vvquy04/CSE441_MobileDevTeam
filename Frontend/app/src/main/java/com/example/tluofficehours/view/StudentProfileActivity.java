package com.example.tluofficehours.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.utils.FileUtils;
import com.example.tluofficehours.utils.SharedPrefsManager;
import com.example.tluofficehours.viewmodel.StudentProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class StudentProfileActivity extends AppCompatActivity {

    private StudentProfileViewModel viewModel;
    private SharedPrefsManager sharedPrefsManager;
    
    // UI Components
    private CircleImageView profileImageView;
    private EditText nameEditText;
    private TextView userRoleTextView;
    private TextView studentIdTextView;
    private EditText classEditText;
    private EditText phoneEditText;
    private TextView emailTextView;
    private Button updateButton;
    private ImageView logoutIcon;
    private BottomNavigationView bottomNavigationView;
    
    // Image picker
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_profile);
        
        // Initialize SharedPrefsManager
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
        
        // Set auth token for API calls
        String token = sharedPrefsManager.getAuthToken();
        if (token != null && !token.isEmpty()) {
            com.example.tluofficehours.api.RetrofitClient.setAuthToken(token);
        }
        
        // Initialize views
        initializeViews();
        
        // Setup image picker
        setupImagePicker();
        
        // Setup click listeners
        setupClickListeners();
        
        // Setup bottom navigation
        setupBottomNavigation();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StudentProfileViewModel.class);
        viewModel.setSharedPrefsManager(sharedPrefsManager);
        
        // Observe data changes
        observeViewModel();
        
        // Load initial data
        viewModel.loadStudentProfile();
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(profileImageView);
                }
            }
        );
    }
    
    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        userRoleTextView = findViewById(R.id.userRoleTextView);
        studentIdTextView = findViewById(R.id.studentIdTextView);
        classEditText = findViewById(R.id.classEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailTextView = findViewById(R.id.emailTextView);
        updateButton = findViewById(R.id.updateButton);
        logoutIcon = findViewById(R.id.topRightIcon);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    
    private void observeViewModel() {
        // Observe user profile data
        viewModel.getStudentProfile().observe(this, studentProfile -> {
            if (studentProfile != null && studentProfile.getProfile() != null) {
                updateUI(studentProfile);
            }
        });
        
        // Observe avatar URL
        viewModel.getAvatarUrl().observe(this, avatarUrl -> {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(profileImageView);
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            updateButton.setEnabled(!isLoading);
            updateButton.setText(isLoading ? "Đang cập nhật..." : "Cập nhật");
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
        
        // Observe update success
        viewModel.getUpdateSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                selectedImageUri = null;
                
                // Clear focus from all EditText fields to allow UI update
                nameEditText.clearFocus();
                classEditText.clearFocus();
                phoneEditText.clearFocus();
                
                // Force update UI after a short delay to ensure data is loaded
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    android.util.Log.d("StudentProfileActivity", "Forcing UI update after successful update");
                    forceUpdateUI();
                    viewModel.resetUpdateSuccess();
                }, 500);
            }
        });
    }
    
    private void updateUI(com.example.tluofficehours.model.StudentProfile studentProfile) {
        android.util.Log.d("StudentProfileActivity", "updateUI called with profile: " + studentProfile.toString());
        
        // Set user role
        userRoleTextView.setText("Sinh viên");
        
        // Update student UI
        com.example.tluofficehours.model.StudentProfile.Profile profile = studentProfile.getProfile();
        if (profile != null) {
            android.util.Log.d("StudentProfileActivity", "Profile data: name=" + profile.getStudentName() + 
                ", phone=" + profile.getPhoneNumber() + ", class=" + profile.getClassName());
            
            // Only update EditText fields if they don't have focus (user is not currently editing)
            if (!nameEditText.hasFocus()) {
                String newName = profile.getStudentName() != null ? profile.getStudentName() : "";
                android.util.Log.d("StudentProfileActivity", "Setting name to: " + newName);
                nameEditText.setText(newName);
            } else {
                android.util.Log.d("StudentProfileActivity", "Name field has focus, skipping update");
            }
            
            if (!classEditText.hasFocus()) {
                String newClass = profile.getClassName() != null ? profile.getClassName() : "";
                android.util.Log.d("StudentProfileActivity", "Setting class to: " + newClass);
                classEditText.setText(newClass);
            } else {
                android.util.Log.d("StudentProfileActivity", "Class field has focus, skipping update");
            }
            
            if (!phoneEditText.hasFocus()) {
                String newPhone = profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "";
                android.util.Log.d("StudentProfileActivity", "Setting phone to: " + newPhone);
                phoneEditText.setText(newPhone);
            } else {
                android.util.Log.d("StudentProfileActivity", "Phone field has focus, skipping update");
            }
            
            // Always update read-only fields
            studentIdTextView.setText(profile.getStudentCode() != null ? profile.getStudentCode() : "Chưa cập nhật");
            
            // Clear any error states
            nameEditText.setError(null);
            phoneEditText.setError(null);
        }
        
        // Set user email
        if (studentProfile.getUser() != null) {
            emailTextView.setText(studentProfile.getUser().getEmail());
        }
    }
    
    private void setupClickListeners() {
        // Profile image click to pick new image
        profileImageView.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
        
        // Update button click
        updateButton.setOnClickListener(v -> {
            saveProfile();
        });
        
        // Logout icon click
        logoutIcon.setOnClickListener(v -> {
            showLogoutDialog();
        });
    }
    
    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String className = classEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        
        // Validate input
        if (name.isEmpty()) {
            nameEditText.setError("Vui lòng nhập họ và tên");
            return;
        }
        
        if (phone.isEmpty()) {
            phoneEditText.setError("Vui lòng nhập số điện thoại");
            return;
        }
        
        // Log the data being sent
        android.util.Log.d("StudentProfileActivity", "Saving profile: name=" + name + ", phone=" + phone + ", class=" + className);
        
        // Convert selected image URI to file path if exists
        String imagePath = null;
        if (selectedImageUri != null) {
            try {
                imagePath = FileUtils.getPath(this, selectedImageUri);
                android.util.Log.d("StudentProfileActivity", "Image path: " + imagePath);
                // TODO: Handle image upload separately if needed
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Use main update method (JSON format)
        viewModel.updateStudentProfile(name, phone, className);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Có", (dialog, which) -> {
                logout();
            })
            .setNegativeButton("Không", null)
            .show();
    }
    
    private void logout() {
        // Clear stored data
        sharedPrefsManager.clearUserData();
        
        // Show success message
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        
        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Navigate to home
                Intent intent = new Intent(this, StudentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                // Navigate to MyAppointmentActivity
                Intent intent = new Intent(this, com.example.tluofficehours.MyAppointmentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Already on profile, do nothing
                return true;
            }
            return false;
        });
        
        // Set profile as selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
    
    private void forceUpdateUI() {
        // Force update all EditText fields regardless of focus
        if (viewModel.getStudentProfile().getValue() != null && 
            viewModel.getStudentProfile().getValue().getProfile() != null) {
            
            com.example.tluofficehours.model.StudentProfile.Profile profile = 
                viewModel.getStudentProfile().getValue().getProfile();
            
            nameEditText.setText(profile.getStudentName() != null ? profile.getStudentName() : "");
            classEditText.setText(profile.getClassName() != null ? profile.getClassName() : "");
            phoneEditText.setText(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "");
            
            android.util.Log.d("StudentProfileActivity", "Force updated UI with latest data");
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile data when returning to this activity
        viewModel.loadStudentProfile();
    }
} 