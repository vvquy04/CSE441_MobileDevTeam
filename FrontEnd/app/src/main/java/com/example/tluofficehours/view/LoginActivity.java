package com.example.tluofficehours.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

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
//HEAD
    private SharedPreferences sharedPreferences;
//
    private SharedPrefsManager sharedPrefsManager;
// vanquy_refactor
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
//HEAD
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TLUOfficeHours", MODE_PRIVATE);
        
        // Log để kiểm tra SharedPreferences
        Log.d("LoginActivity", "SharedPreferences initialized: " + (sharedPreferences != null ? "YES" : "NO"));
        Log.d("LoginActivity", "Context: " + getApplicationContext().getPackageName());
        
        // AUTO-LOGIN: Nếu đã có token, chuyển thẳng vào trang chủ
        String token = sharedPreferences.getString("auth_token", null);
        String userEmail = sharedPreferences.getString("user_email", null);
        String userRoles = sharedPreferences.getString("user_roles", null);
        
        Log.d("LoginActivity", "Auto-login check - Token: " + (token != null ? "EXISTS" : "NULL"));
        Log.d("LoginActivity", "Auto-login check - Email: " + userEmail);
        Log.d("LoginActivity", "Auto-login check - Roles: " + userRoles);
        
        if (token != null && userEmail != null) {
            // Nếu có roles, kiểm tra vai trò để chuyển đúng màn hình
            boolean isFaculty = userRoles != null && userRoles.contains("faculty");
            boolean isStudent = userRoles != null && userRoles.contains("student");
            
            Log.d("LoginActivity", "Auto-login - isFaculty: " + isFaculty + ", isStudent: " + isStudent);
            
            if (isFaculty) {
                Log.d("LoginActivity", "Auto-login: Redirecting to FacultyMainActivity");
                Intent intent = new Intent(LoginActivity.this, FacultyMainActivity.class);
                startActivity(intent);
                finish();
                return;
            } else if (isStudent) {
                Log.d("LoginActivity", "Auto-login: Redirecting to StudentMainActivity");
                Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        
        Log.d("LoginActivity", "Auto-login: No valid session found, showing login screen");
        
        // Initialize RetrofitClient with context
        RetrofitClient.init(this);
//
        // Initialize SharedPrefsManager
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
// vanquy_refactor
        
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
//HEAD
                    try {
                        String responseStr = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseStr);
                        
                        // Log response để debug
                        Log.d("LoginActivity", "Login response: " + responseStr);
                        Log.d("LoginActivity", "Has token field: " + jsonObject.has("token"));
                        Log.d("LoginActivity", "Has access_token field: " + jsonObject.has("access_token"));
                        
                        // Save token
                        if (jsonObject.has("token")) {
                            String token = jsonObject.getString("token");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("auth_token", token);
                            editor.putString("user_email", email);
                            editor.apply();
                            
                            // Log để kiểm tra token đã được lưu
                            String savedToken = sharedPreferences.getString("auth_token", null);
                            Log.d("LoginActivity", "Token saved successfully: " + (savedToken != null ? "YES" : "NO"));
                            if (savedToken != null) {
                                Log.d("LoginActivity", "Saved token length: " + savedToken.length());
                                Log.d("LoginActivity", "Saved token preview: " + savedToken.substring(0, Math.min(20, savedToken.length())) + "...");
                            }
                        } else if (jsonObject.has("access_token")) {
                            // Nếu API trả về access_token thay vì token
                            String token = jsonObject.getString("access_token");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("auth_token", token);
                            editor.putString("user_email", email);
                            
                            // Lưu roles
                            if (jsonObject.has("roles")) {
                                JSONArray rolesArray = jsonObject.getJSONArray("roles");
                                StringBuilder rolesStr = new StringBuilder();
                                for (int i = 0; i < rolesArray.length(); i++) {
                                    rolesStr.append(rolesArray.getString(i));
                                    if (i < rolesArray.length() - 1) rolesStr.append(",");
                                }
                                editor.putString("user_roles", rolesStr.toString());
                                Log.d("LoginActivity", "Roles saved: " + rolesStr.toString());
                            }
                            
                            editor.apply();
                            
                            Log.d("LoginActivity", "Access token saved successfully");
                            String savedToken = sharedPreferences.getString("auth_token", null);
                            String savedRoles = sharedPreferences.getString("user_roles", null);
                            Log.d("LoginActivity", "Access token saved: " + (savedToken != null ? "YES" : "NO"));
                            Log.d("LoginActivity", "Roles saved: " + (savedRoles != null ? "YES" : "NO"));
                        } else {
                            Log.e("LoginActivity", "No token field found in response");
                        }
                        
                        JSONArray rolesArray = jsonObject.getJSONArray("roles");
                        boolean isStudent = false, isFaculty = false;
                        for (int i = 0; i < rolesArray.length(); i++) {
                            String role = rolesArray.getString(i);
                            if (role.equalsIgnoreCase("student")) isStudent = true;
                            if (role.equalsIgnoreCase("faculty")) isFaculty = true;
                        }
                        
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
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
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
// vanquy_refactor
                    }
                } else {
                    android.util.Log.e("LoginActivity", "Login failed: " + response.code() + " - " + response.message());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
//HEAD
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
// vanquy_refactor
            }
        });
    }
}

