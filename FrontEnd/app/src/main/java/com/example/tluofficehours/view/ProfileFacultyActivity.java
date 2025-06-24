package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.FacultyProfile;
import com.example.tluofficehours.viewmodel.ProfileFacultyViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFacultyActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView facultyIdTextView, departmentTextView, phoneTextView, emailTextView, userNameTextView;
    private Button updateButton;
    private ProfileFacultyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_faculty);
        
        initViews();
        setupViewModel();
        setupBottomNavigation();
        setupClickListeners();
        loadFacultyData();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        facultyIdTextView = findViewById(R.id.facultyIdTextView);
        departmentTextView = findViewById(R.id.departmentTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        updateButton = findViewById(R.id.updateButton);
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
            // Handle update profile
            Toast.makeText(this, "Chức năng cập nhật sẽ được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFacultyData() {
        viewModel.loadFacultyProfile();
    }

    private void updateUI(FacultyProfile profile) {
        if (profile != null) {
            userNameTextView.setText(
                profile.getFacultyName() != null && !profile.getFacultyName().isEmpty()
                ? profile.getFacultyName() : "Chưa cập nhật"
            );
            departmentTextView.setText(
                profile.getDepartmentName() != null && !profile.getDepartmentName().isEmpty()
                ? profile.getDepartmentName() : "Chưa cập nhật"
            );
            phoneTextView.setText(
                profile.getPhoneNumber() != null && !profile.getPhoneNumber().isEmpty()
                ? profile.getPhoneNumber() : "Chưa cập nhật"
            );
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