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
import com.example.tluofficehours.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {
    private TextView txtRegister;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefsManager = SharedPrefsManager.getInstance(this);

        txtRegister = findViewById(R.id.txt_register);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);

        // Auto-login nếu đã có token và role
        String token = sharedPrefsManager.getAuthToken();
        String userRole = sharedPrefsManager.getUserRole();
        if (token != null && userRole != null) {
            if (userRole.equals("faculty")) {
                startActivity(new Intent(LoginActivity.this, FacultyMainActivity.class));
                finish();
                return;
            } else if (userRole.equals("student")) {
                startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                finish();
                return;
            }
        }

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
        AuthRepository authRepository = new AuthRepository();
        LoginRequest loginRequest = new LoginRequest(email, password);
        authRepository.login(loginRequest).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccessToken();
                    sharedPrefsManager.saveAuthToken(accessToken);
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
                    sharedPrefsManager.saveUserRole(userRole);

                    if (isStudent) {
                        startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                        finish();
                    } else if (isFaculty) {
                        startActivity(new Intent(LoginActivity.this, FacultyMainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Không xác định được vai trò!", Toast.LENGTH_SHORT).show();
                    }
                } else {
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

