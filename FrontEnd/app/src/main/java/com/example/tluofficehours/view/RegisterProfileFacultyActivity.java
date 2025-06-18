package com.example.tluofficehours.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Department;
import com.example.tluofficehours.viewmodel.RegisterFacultyViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterProfileFacultyActivity extends AppCompatActivity {

    private EditText edtFacultyName, edtDegree, edtPhoneNumber, edtOfficeRoom;
    private TextView txtFacultyName, txtDegree, txtPhoneNumber, txtOfficeRoom;
    private Spinner spinnerDepartment;
    private LinearLayout btnContinueLayout;
    private TextView btnContinue;
    private RegisterFacultyViewModel viewModel;

    private String email;
    private String password;
    private List<Department> departments = new ArrayList<>();
    private Department selectedDepartment;

    private ImageView imgProfile;
    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("WrongViewCast")
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
        spinnerDepartment = findViewById(R.id.spinner_department);
        edtDegree = findViewById(R.id.edt_degree);
        edtPhoneNumber = findViewById(R.id.edt_phone);
        edtOfficeRoom = findViewById(R.id.edt_office);
        btnContinueLayout = findViewById(R.id.layout_continue_button);
        btnContinue = findViewById(R.id.btn_continue);
        imgProfile = findViewById(R.id.img_profile);

        txtFacultyName = findViewById(R.id.txt_faculty_name_label);
        txtDegree = findViewById(R.id.txt_degree_label);
        txtPhoneNumber = findViewById(R.id.txt_phone_label);
        txtOfficeRoom = findViewById(R.id.txt_office_label);

        // Load departments
        loadDepartments();

        // Set click listener for choosing image
        TextView txtChooseImage = findViewById(R.id.txt_choose_image);
        txtChooseImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        });

        // Observe LiveData for success/error messages
        viewModel.getSuccessMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            // Navigate to success screen
            Intent successIntent = new Intent(RegisterProfileFacultyActivity.this, RegisterSuccessActivity.class);
            startActivity(successIntent);
            finish();
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        // Set click listener for register button
        btnContinueLayout.setOnClickListener(v -> {
            // Reset errors
            txtFacultyName.setError(null);
            txtDegree.setError(null);
            txtPhoneNumber.setError(null);
            txtOfficeRoom.setError(null);

            String facultyName = edtFacultyName.getText().toString().trim();
            String degree = edtDegree.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String officeRoom = edtOfficeRoom.getText().toString().trim();

            boolean isValid = true;

            if (facultyName.isEmpty()) {
                txtFacultyName.setError("Tên giảng viên không được để trống");
                isValid = false;
            }
            if (selectedDepartment == null) {
                Toast.makeText(this, "Vui lòng chọn bộ môn", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            // Kiểm tra email kết thúc bằng @tlu.edu.vn
            if (email == null || !email.endsWith("@tlu.edu.vn")) {
                Toast.makeText(this, "Email giảng viên phải có đuôi @tlu.edu.vn", Toast.LENGTH_LONG).show();
                isValid = false;
            }

            if (isValid) {
                viewModel.registerFaculty(email, password, facultyName, selectedDepartment.getDepartmentId(), degree, phoneNumber, officeRoom);
            }
        });
    }

    private void loadDepartments() {
        viewModel.getAuthRepository().getDepartments().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departments = response.body();
                    setupDepartmentSpinner();
                } else {
                    Toast.makeText(RegisterProfileFacultyActivity.this, "Không thể tải danh sách bộ môn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                Toast.makeText(RegisterProfileFacultyActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDepartmentSpinner() {
        ArrayAdapter<Department> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);
        
        spinnerDepartment.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedDepartment = departments.get(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedDepartment = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}
