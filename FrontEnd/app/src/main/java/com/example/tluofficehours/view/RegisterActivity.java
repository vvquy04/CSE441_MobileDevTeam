package com.example.tluofficehours.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.utils.EmailValidator;
import com.example.tluofficehours.viewmodel.RegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private TextInputLayout emailInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    private TextView txtLogin;
    private Button btnRegister;
    private RegisterViewModel viewModel; // Khai báo nhưng không khởi tạo ở đây

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ViewModel trong onCreate
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Ánh xạ view
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        txtLogin = findViewById(R.id.txt_login);
        btnRegister = findViewById(R.id.btn_register);

        // Thiết lập TextInputLayout (đảm bảo ID tồn tại trong layout XML)
        emailInputLayout = findViewById(R.id.txt_email);
        passwordInputLayout = findViewById(R.id.txt_password);
        confirmPasswordInputLayout = findViewById(R.id.txt_confirm_password);

        // Quan sát LiveData để hiện thông báo
        viewModel.getSuccessMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        // Bắt sự kiện click nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Reset lỗi
            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmPasswordInputLayout.setError(null);

            boolean isValid = true;

            if (!EmailValidator.isValidTLUEmail(email)) {
                emailInputLayout.setError("Email phải có đuôi @e.tlu.edu.vn (sinh viên) hoặc @tlu.edu.vn (giảng viên)");
                isValid = false;
            }

            if (password.isEmpty() || password.length() < 6) {
                passwordInputLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
                isValid = false;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInputLayout.setError("Mật khẩu không khớp");
                isValid = false;
            }

            if (isValid) {
                // Auto-detect loại đăng ký dựa trên email
                String userType = EmailValidator.detectUserType(email);
                navigateToRegistrationForm(userType, email, password);
            }
        });
    }

    private void navigateToRegistrationForm(String userType, String email, String password) {
        Intent intent;
        if ("student".equals(userType)) {
            // Đăng ký sinh viên
            intent = new Intent(RegisterActivity.this, RegisterProfileStudentActivity.class);
            Toast.makeText(this, "Email của bạn được nhận diện là sinh viên. Chuyển đến form đăng ký sinh viên", Toast.LENGTH_LONG).show();
        } else {
            // Đăng ký giảng viên
            intent = new Intent(RegisterActivity.this, RegisterProfileFacultyActivity.class);
            Toast.makeText(this, "Email của bạn được nhận diện là giảng viên. Chuyển đến form đăng ký giảng viên", Toast.LENGTH_LONG).show();
        }
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}