package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.viewmodel.AppointmentViewModel;

public class BookingSuccessActivity extends AppCompatActivity {

    private TextView teacherNameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView roomTextView;
    private TextView contactEmailTextView;
    private Button backToHomeButton;
    private AppointmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Ánh xạ các View
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        roomTextView = findViewById(R.id.roomTextView);
        contactEmailTextView = findViewById(R.id.contactEmailTextView);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Lấy dữ liệu từ Intent và set vào ViewModel
        Intent intent = getIntent();
        String teacherName = intent.getStringExtra("teacherName");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String room = intent.getStringExtra("room");
        String contactEmail = intent.getStringExtra("contactEmail");

        viewModel.setBookingData(teacherName, date, time, room, contactEmail);

        // Observe ViewModel data
        observeViewModel();

        // Xử lý sự kiện cho nút "VỀ TRANG CHỦ"
        backToHomeButton.setOnClickListener(v -> {
            Intent intentToHome = new Intent(BookingSuccessActivity.this, StudentMainActivity.class);
            intentToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentToHome);
            finish();
        });
    }

    private void observeViewModel() {
        // Observe teacher name
        viewModel.getTeacherName().observe(this, teacherName -> {
            teacherNameTextView.setText(teacherName != null ? teacherName : "");
        });

        // Observe date
        viewModel.getDate().observe(this, date -> {
            dateTextView.setText(date != null ? date : "");
        });

        // Observe time
        viewModel.getTime().observe(this, time -> {
            timeTextView.setText(time != null ? time : "");
        });

        // Observe room
        viewModel.getRoom().observe(this, room -> {
            roomTextView.setText(room != null ? room : "");
        });

        // Observe contact email
        viewModel.getContactEmail().observe(this, contactEmail -> {
            contactEmailTextView.setText(contactEmail != null ? contactEmail : "");
        });
    }
}