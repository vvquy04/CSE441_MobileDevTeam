package com.example.tluofficehours.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NotificationFacultyActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private NotificationFacultyAdapter adapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_faculty);
        
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupBackButton();
        loadFacultyNotifications();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.notificationRecyclerView);
        backButton = findViewById(R.id.backButton);
    }

    private void setupRecyclerView() {
        adapter = new NotificationFacultyAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(notification -> {
            // Handle notification click for faculty
            handleNotificationClick(notification);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void loadFacultyNotifications() {
        // Create sample notifications specifically for faculty
        List<Notification> notifications = new ArrayList<>();
        
        notifications.add(new Notification(
            1L,
            "Lịch hẹn mới từ sinh viên",
            "Sinh viên Nguyễn Văn A đã đặt lịch hẹn với bạn vào ngày mai lúc 10:00. Vui lòng xác nhận hoặc từ chối.",
            "booking",
            false,
            "2025-05-23T14:30:00Z"
        ));
        
        notifications.add(new Notification(
            2L,
            "Nhắc nhở lịch hẹn sắp tới",
            "Bạn có lịch hẹn với sinh viên Trần Thị B trong 30 phút nữa. Vui lòng chuẩn bị.",
            "reminder",
            false,
            "2025-05-23T12:00:00Z"
        ));
        
        notifications.add(new Notification(
            3L,
            "Lịch hẹn bị hủy",
            "Sinh viên Lê Văn C đã hủy lịch hẹn vào ngày 25/5/2025. Lịch trống đã được cập nhật.",
            "cancellation",
            false,
            "2025-05-22T16:45:00Z"
        ));
        
        notifications.add(new Notification(
            4L,
            "Cập nhật lịch làm việc",
            "Hệ thống đã cập nhật lịch làm việc của bạn. Vui lòng kiểm tra lại thông tin.",
            "system",
            false,
            "2025-05-21T09:15:00Z"
        ));
        
        notifications.add(new Notification(
            5L,
            "Sinh viên đã xác nhận lịch hẹn",
            "Sinh viên Phạm Thị D đã xác nhận lịch hẹn vào ngày 26/5/2025 lúc 14:00.",
            "confirmation",
            false,
            "2025-05-20T15:20:00Z"
        ));
        
        adapter.setNotifications(notifications);
    }

    private void handleNotificationClick(Notification notification) {
        // Handle different types of notifications for faculty
        switch (notification.getType()) {
            case "booking":
                // Navigate to calendar to approve/reject
                Toast.makeText(this, "Chuyển đến quản lý lịch để xử lý lịch hẹn", Toast.LENGTH_SHORT).show();
                Intent calendarIntent = new Intent(this, FacultyCalendarActivity.class);
                startActivity(calendarIntent);
                break;
            case "reminder":
                // Show reminder details
                Toast.makeText(this, "Nhắc nhở: " + notification.getMessage(), Toast.LENGTH_LONG).show();
                break;
            case "cancellation":
                // Show cancellation details
                Toast.makeText(this, "Lịch hẹn đã bị hủy", Toast.LENGTH_SHORT).show();
                break;
            case "system":
                // Show system update details
                Toast.makeText(this, "Thông báo hệ thống: " + notification.getMessage(), Toast.LENGTH_LONG).show();
                break;
            case "confirmation":
                // Show confirmation details
                Toast.makeText(this, "Sinh viên đã xác nhận lịch hẹn", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Clicked: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.navigation_home) {
            Intent intent = new Intent(this, FacultyMainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_calendar) {
            Intent intent = new Intent(this, FacultyCalendarActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_profile) {
            Intent intent = new Intent(this, ProfileFacultyActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        return false;
    }
} 