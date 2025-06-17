package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.viewmodel.RegisterFacultyViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterProfileFacultyActivity extends AppCompatActivity {

    private EditText edtFacultyName, edtDepartment, edtDegree, edtPhoneNumber, edtOfficeRoom;
    private TextInputLayout txtFacultyName, txtDepartment, txtDegree, txtPhoneNumber, txtOfficeRoom;
    private Button btnRegisterProfile;
    private RegisterFacultyViewModel viewModel;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_faculty);

        // Get data from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterFacultyViewModel.class);

        // Map views
        edtFacultyName = findViewById(R.id.edt_faculty_name);
        edtDepartment = findViewById(R.id.edt_department);
        edtDegree = findViewById(R.id.edt_degree);
        edtPhoneNumber = findViewById(R.id.edt_phone_number);
        edtOfficeRoom = findViewById(R.id.edt_office_room);
        btnRegisterProfile = findViewById(R.id.btn_register_profile);

        txtFacultyName = findViewById(R.id.txt_faculty_name);
        txtDepartment = findViewById(R.id.txt_department);
        txtDegree = findViewById(R.id.txt_degree);
        txtPhoneNumber = findViewById(R.id.txt_phone_number);
        txtOfficeRoom = findViewById(R.id.txt_office_room);

        // Observe LiveData for success/error messages
        viewModel.getSuccessMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            // Optionally navigate to login or dashboard after successful registration
            Intent loginIntent = new Intent(RegisterProfileFacultyActivity.this, LoginActivity.class); // Assuming LoginActivity exists
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        // Set click listener for register button
        btnRegisterProfile.setOnClickListener(v -> {
            // Reset errors
            txtFacultyName.setError(null);
            txtDepartment.setError(null);
            txtDegree.setError(null);
            txtPhoneNumber.setError(null);
            txtOfficeRoom.setError(null);

            String facultyName = edtFacultyName.getText().toString().trim();
            String departmentId = edtDepartment.getText().toString().trim();
            String degree = edtDegree.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String officeRoom = edtOfficeRoom.getText().toString().trim();

            boolean isValid = true;

            if (facultyName.isEmpty()) {
                txtFacultyName.setError("Tên giảng viên không được để trống");
                isValid = false;
            }
            if (departmentId.isEmpty()) {
                txtDepartment.setError("Mã bộ môn không được để trống");
                isValid = false;
            }

            if (isValid) {
                viewModel.registerFaculty(email, password, facultyName, departmentId, degree, phoneNumber, officeRoom);
            }
        });
    }
} 