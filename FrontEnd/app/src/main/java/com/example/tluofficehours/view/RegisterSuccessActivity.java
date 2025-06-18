package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tluofficehours.R;

public class RegisterSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_success);

        LinearLayout btnBackToLogin = findViewById(R.id.btn_back_to_login);
        btnBackToLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegisterSuccessActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}
