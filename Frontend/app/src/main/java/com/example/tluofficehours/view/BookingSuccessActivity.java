package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tluofficehours.view.StudentMainActivity;

public class BookingSuccessActivity extends AppCompatActivity {

    private TextView teacherNameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView roomTextView; // Khai báo TextView mới
    private TextView contactEmailTextView;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        // Ánh xạ các View
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        roomTextView = findViewById(R.id.roomTextView); // Ánh xạ TextView mới
        contactEmailTextView = findViewById(R.id.contactEmailTextView);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Đặt dữ liệu mẫu (bạn có thể nhận dữ liệu này qua Intent từ Activity đặt lịch hẹn)
        Intent intent = getIntent();
        String teacherName = intent.getStringExtra("teacherName");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String room = intent.getStringExtra("room"); // Có thể null nếu chưa truyền
        String contactEmail = intent.getStringExtra("contactEmail"); // Có thể null nếu chưa truyền

        // Gán dữ liệu vào TextViews
        teacherNameTextView.setText(teacherName != null ? teacherName : "");
        dateTextView.setText(date != null ? date : "");
        timeTextView.setText(time != null ? time : "");
        roomTextView.setText(room != null ? room : "");
        contactEmailTextView.setText(contactEmail != null ? contactEmail : "");

        // Xử lý sự kiện cho nút "VỀ TRANG CHỦ"
        backToHomeButton.setOnClickListener(v -> {
            // Ví dụ: Chuyển về MainActivity (trang chủ)
            Intent intentToHome = new Intent(BookingSuccessActivity.this, StudentMainActivity.class);
            intentToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các Activity trước đó
            startActivity(intentToHome);
            finish(); // Đóng Activity hiện tại
        });
    }
}