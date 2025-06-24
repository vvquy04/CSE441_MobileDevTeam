package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.model.Notification;
import com.example.tluofficehours.view.FacultyCalendarActivity;
import com.example.tluofficehours.view.FacultyMainActivity;
import com.example.tluofficehours.view.NotificationAdapter;
import com.example.tluofficehours.view.ProfileFacultyActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupBackButton();
        loadNotifications();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.notificationRecyclerView);
        backButton = findViewById(R.id.backButton);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(notification -> {
            // Handle notification click
            Toast.makeText(this, "Clicked: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
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

    private void loadNotifications() {
        // Create sample notifications for faculty
        List<Notification> notifications = new ArrayList<>();
        
        notifications.add(new Notification(
            "1",
            "Lịch hẹn mới",
            "Sinh viên Nguyễn Văn A đã đặt lịch hẹn với bạn vào ngày mai lúc 10:00.",
            "23/5/2025",
            "14:30",
            "booking"
        ));
        
        notifications.add(new Notification(
            "2",
            "Nhắc nhở lịch hẹn",
            "Bạn có lịch hẹn với sinh viên Trần Thị B trong 30 phút nữa.",
            "23/5/2025",
            "12:00",
            "reminder"
        ));
        
        notifications.add(new Notification(
            "3",
            "Lịch hẹn bị hủy",
            "Sinh viên Lê Văn C đã hủy lịch hẹn vào ngày 25/5/2025.",
            "22/5/2025",
            "16:45",
            "cancellation"
        ));
        
        notifications.add(new Notification(
            "4",
            "Cập nhật hệ thống",
            "Hệ thống đã được cập nhật với các tính năng mới.",
            "21/5/2025",
            "09:15",
            "system"
        ));
        
        adapter.setNotifications(notifications);
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