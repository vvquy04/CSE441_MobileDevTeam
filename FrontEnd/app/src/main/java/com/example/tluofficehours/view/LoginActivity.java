package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.model.LoginRequest;
import com.example.tluofficehours.model.LoginResponse;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import com.example.tluofficehours.utils.SharedPrefsManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextView txtRegister;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private SharedPrefsManager sharedPrefsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize SharedPrefsManager
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
        
        txtRegister = findViewById(R.id.txt_register);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        
        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            login(email, password);
        });
    }
    
    private void login(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    // Save auth token
                    String accessToken = loginResponse.getAccessToken();
                    android.util.Log.d("LoginActivity", "Login successful, token: " + (accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "null"));
                    sharedPrefsManager.saveAuthToken(accessToken);
                    android.util.Log.d("LoginActivity", "Token saved to SharedPrefs: " + sharedPrefsManager.getAuthToken());
                    RetrofitClient.setAuthToken(accessToken);
                    android.util.Log.d("LoginActivity", "Token set to RetrofitClient: " + accessToken);
                    sharedPrefsManager.saveUserEmail(email);
                    
                    String[] roles = loginResponse.getRoles();
                    boolean isStudent = false, isFaculty = false;
                    String userRole = "";
                    
                    for (String role : roles) {
                        if (role.equalsIgnoreCase("student")) {
                            isStudent = true;
                            userRole = "student";
                        }
                        if (role.equalsIgnoreCase("faculty")) {
                            isFaculty = true;
                            userRole = "faculty";
                        }
                    }
                    
                    // Save user role
                    sharedPrefsManager.saveUserRole(userRole);
                    android.util.Log.d("LoginActivity", "User role: " + userRole);
                    
                    if (isStudent) {
                        Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (isFaculty) {
                        Intent intent = new Intent(LoginActivity.this, FacultyMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Không xác định được vai trò!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.util.Log.e("LoginActivity", "Login failed: " + response.code() + " - " + response.message());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

