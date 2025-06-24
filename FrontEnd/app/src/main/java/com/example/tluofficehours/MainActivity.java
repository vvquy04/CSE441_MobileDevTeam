package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.utils.SharedPrefsManager;
import com.example.tluofficehours.view.LoginActivity;
import com.example.tluofficehours.view.StudentMainActivity;
import com.example.tluofficehours.view.FacultyMainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize SharedPrefsManager
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance(this);
        
        // Check if user is logged in
        if (sharedPrefsManager.isLoggedIn()) {
            // Set auth token for API calls
            String token = sharedPrefsManager.getAuthToken();
            RetrofitClient.setAuthToken(token);
            
            // Redirect based on user role
            String userRole = sharedPrefsManager.getUserRole();
            Intent intent;
            
            if ("student".equals(userRole)) {
                intent = new Intent(this, StudentMainActivity.class);
            } else if ("faculty".equals(userRole)) {
                intent = new Intent(this, FacultyMainActivity.class);
            } else {
                // Default to login if role is unknown
                intent = new Intent(this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        } else {
            // User not logged in, go to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}