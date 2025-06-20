package com.example.tluofficehours.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.viewmodel.RegisterStudentViewModel;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;

public class RegisterProfileStudentActivity extends AppCompatActivity {

    private EditText edtStudentName, edtStudentCode, edtClassName, edtPhoneNumber;
    private TextView txtStudentName, txtStudentCode, txtClassName, txtPhoneNumber;
    private LinearLayout btnContinueLayout;
    private Button btnContinue;
    private RegisterStudentViewModel viewModel;

    private String email;
    private String password;

    private ImageView imgProfile;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_READ_IMAGE = 1001;

    private Uri selectedImageUri = null;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_student);

        // Get data from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterStudentViewModel.class);

        // Map views
        edtStudentName = findViewById(R.id.input_student_name);
        edtStudentCode = findViewById(R.id.input_student_id);
        edtClassName = findViewById(R.id.input_class);
        edtPhoneNumber = findViewById(R.id.input_phone);
        btnContinueLayout = findViewById(R.id.btn_continue);
        btnContinue = findViewById(R.id.txt_continue);
        imgProfile = findViewById(R.id.img_profile);

        txtStudentName = findViewById(R.id.txt_student_name_label);
        txtStudentCode = findViewById(R.id.txt_student_id_label);
        txtClassName = findViewById(R.id.txt_class_label);
        txtPhoneNumber = findViewById(R.id.txt_phone_label);

        // Set click listener for choosing image
        TextView txtChooseImage = findViewById(R.id.txt_choose_image);
        txtChooseImage.setOnClickListener(v -> {
            if (checkAndRequestImagePermission()) {
                openImagePicker();
            }
        });

        // Observe LiveData for success/error messages
        viewModel.getSuccessMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            // Navigate to success screen
            Intent successIntent = new Intent(RegisterProfileStudentActivity.this, RegisterSuccessActivity.class);
            startActivity(successIntent);
            finish();
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        // Set click listener for register button
        btnContinueLayout.setOnClickListener(v -> {
            handleRegistration();
        });
        
        btnContinue.setOnClickListener(v -> {
            handleRegistration();
        });
    }

    private void handleRegistration() {
        // Reset errors
        txtStudentName.setError(null);
        txtStudentCode.setError(null);
        txtClassName.setError(null);
        txtPhoneNumber.setError(null);

        String studentName = edtStudentName.getText().toString().trim();
        String studentCode = edtStudentCode.getText().toString().trim();
        String className = edtClassName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();

        boolean isValid = true;

        if (studentName.isEmpty()) {
            txtStudentName.setError("Tên sinh viên không được để trống");
            isValid = false;
        }
        if (studentCode.isEmpty()) {
            txtStudentCode.setError("Mã sinh viên không được để trống");
            isValid = false;
        }
        // Kiểm tra email kết thúc bằng @e.tlu.edu.vn
        if (email == null || !email.endsWith("@e.tlu.edu.vn")) {
            Toast.makeText(this, "Email sinh viên phải có đuôi @e.tlu.edu.vn", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (isValid) {
            viewModel.registerStudent(this, email, password, studentName, studentCode, className, phoneNumber, selectedImageUri);
        }
    }

    private boolean checkAndRequestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_IMAGE);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_IMAGE);
                return false;
            }
        }
        return true;
    }

    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền truy cập ảnh để chọn ảnh đại diện!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}