package com.example.tluofficehours.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
// HEAD
import android.util.Log;
import android.view.MenuItem;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
//
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
// vanquy_refactor
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.view.NotificationFacultyActivity;
import com.example.tluofficehours.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.tluofficehours.viewmodel.FacultyMainViewModel;
import com.example.tluofficehours.adapter.FacultyCalendarAdapter;

public class FacultyMainActivity extends AppCompatActivity {
// HEAD

    private static final String TAG = "FacultyMainActivity";
    private BottomNavigationView bottomNavigationView;
    private ImageView notificationIcon;
    private FloatingActionButton fabAddSchedule;
    private FacultyMainViewModel viewModel;
    private TextView tvAvailableSlots, tvBookingsCount;
    private RecyclerView rvUpcomingAppointments;
    private FacultyCalendarAdapter appointmentAdapter;
    private TextView tvNoUpcomingAppointments;

    //
// vanquy_refactor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_main);

        // Đảm bảo luôn set lại token cho RetrofitClient khi vào màn faculty
        com.example.tluofficehours.utils.SharedPrefsManager sharedPrefsManager = com.example.tluofficehours.utils.SharedPrefsManager.getInstance(this);
        String token = sharedPrefsManager.getAuthToken();
        if (token != null && !token.isEmpty()) {
            com.example.tluofficehours.api.RetrofitClient.setAuthToken(token);
        }

        Log.d(TAG, "onCreate: Setting up FacultyMainActivity");
        initViews();
        setupNavigation();
        setupNotificationIcon();
        setupFab();
        setupViewModel();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        notificationIcon = findViewById(R.id.notification_icon);
        fabAddSchedule = findViewById(R.id.fab_add_schedule);
        tvAvailableSlots = findViewById(R.id.tv_available_slots);
        tvBookingsCount = findViewById(R.id.tv_bookings_count);
        rvUpcomingAppointments = findViewById(R.id.rv_upcoming_appointments);
        tvNoUpcomingAppointments = new TextView(this);
        tvNoUpcomingAppointments.setText("Không có lịch hẹn nào trong tuần này");
        tvNoUpcomingAppointments.setTextSize(16);
        tvNoUpcomingAppointments.setTextColor(getResources().getColor(R.color.light_blue_200));
        tvNoUpcomingAppointments.setGravity(Gravity.CENTER);
        tvNoUpcomingAppointments.setVisibility(View.GONE);

        if (bottomNavigationView != null) {
            Log.d(TAG, "initViews: BottomNavigationView found");
        } else {
            Log.e(TAG, "initViews: BottomNavigationView is null!");
        }

        if (notificationIcon != null) {
            Log.d(TAG, "initViews: Notification icon found");
        } else {
            Log.e(TAG, "initViews: Notification icon is null!");
        }
    }

    private void setupNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            Log.d(TAG, "setupNavigation: Navigation setup completed");
        } else {
            Log.e(TAG, "setupNavigation: BottomNavigationView is null!");
        }
    }

    private void setupNotificationIcon() {
        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                Log.d(TAG, "setupNotificationIcon: Notification icon clicked");
                Intent intent = new Intent(this, NotificationFacultyActivity.class);
                startActivity(intent);
            });
            Log.d(TAG, "setupNotificationIcon: Notification icon click listener set");
        } else {
            Log.e(TAG, "setupNotificationIcon: Notification icon is null!");
        }
    }

    private void setupFab() {
        if (fabAddSchedule != null) {
            fabAddSchedule.setOnClickListener(v -> {
                Log.d(TAG, "setupFab: FAB clicked");
                Intent intent = new Intent(this, AddScheduleActivity.class);
                startActivity(intent);
            });
            Log.d(TAG, "setupFab: FAB click listener set");
        } else {
            Log.e(TAG, "setupFab: FAB is null!");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FacultyMainViewModel.class);
        viewModel.getDashboardData().observe(this, dashboard -> {
            if (dashboard != null) {
                // Không binding số slot và sinh viên ở đây nữa, sẽ dùng LiveData động bên dưới
            }
        });
        // Quan sát số slot trống và số sinh viên đặt lịch hôm nay
        viewModel.getTodayAvailableSlotCount().observe(this, count -> {
            if (tvAvailableSlots != null) {
                if (count == null || count == 0) {
                    tvAvailableSlots.setText("Không có lịch hẹn nào ngày hôm nay");
                    tvAvailableSlots.setTextSize(16);
                    tvAvailableSlots.setTextColor(getResources().getColor(R.color.light_blue_200));
                    tvAvailableSlots.setTypeface(null, Typeface.NORMAL);
                    tvAvailableSlots.setGravity(Gravity.CENTER);
                } else {
                    tvAvailableSlots.setText(count + " Slot");
                    tvAvailableSlots.setTextSize(32);
                    tvAvailableSlots.setTextColor(getResources().getColor(android.R.color.white));
                    tvAvailableSlots.setTypeface(null, Typeface.BOLD);
                    tvAvailableSlots.setGravity(Gravity.START);
                }
            }
        });
        viewModel.getTodayBookingCount().observe(this, count -> {
            if (tvAvailableSlots != null && tvBookingsCount != null) {
                if (count == null || count == 0) {
                    tvAvailableSlots.setText("Không có lịch hẹn nào ngày hôm nay");
                    tvAvailableSlots.setTextSize(16);
                    tvAvailableSlots.setTextColor(getResources().getColor(R.color.light_blue_200));
                    tvAvailableSlots.setTypeface(null, Typeface.NORMAL);
                    tvAvailableSlots.setGravity(Gravity.CENTER);
                    tvBookingsCount.setText("");
                } else {
                    // Có lịch hẹn hôm nay, hiển thị số slot và số sinh viên
                    Integer slotCount = viewModel.getTodayAvailableSlotCount().getValue();
                    tvAvailableSlots.setText((slotCount != null ? slotCount : 0) + " Slot");
                    tvAvailableSlots.setTextSize(32);
                    tvAvailableSlots.setTextColor(getResources().getColor(android.R.color.white));
                    tvAvailableSlots.setTypeface(null, Typeface.BOLD);
                    tvAvailableSlots.setGravity(Gravity.START);
                    tvBookingsCount.setText(count + " Sinh viên đã đặt lịch");
                }
            }
        });

        appointmentAdapter = new FacultyCalendarAdapter();
        rvUpcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvUpcomingAppointments.setAdapter(appointmentAdapter);
        viewModel.getRecentBookings().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                appointmentAdapter.updateAppointments(bookings);
                rvUpcomingAppointments.setVisibility(View.VISIBLE);
                tvNoUpcomingAppointments.setVisibility(View.GONE);
            } else {
                appointmentAdapter.updateAppointments(null);
                rvUpcomingAppointments.setVisibility(View.GONE);
                if (tvNoUpcomingAppointments.getParent() == null) {
                    View parent = (View) rvUpcomingAppointments.getParent();
                    if (parent instanceof LinearLayout) {
                        ((LinearLayout) parent).addView(tvNoUpcomingAppointments);
                    } else if (parent instanceof androidx.constraintlayout.widget.ConstraintLayout) {
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        params.topToBottom = rvUpcomingAppointments.getId();
                        tvNoUpcomingAppointments.setLayoutParams(params);
                        ((androidx.constraintlayout.widget.ConstraintLayout) parent).addView(tvNoUpcomingAppointments);
                    }
                }
                tvNoUpcomingAppointments.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: Item clicked with ID: " + itemId);

        if (itemId == R.id.navigation_home) {
            Log.d(TAG, "onNavigationItemSelected: Home selected");
            // Already on home
            return true;
        } else if (itemId == R.id.navigation_calendar) {
            Log.d(TAG, "onNavigationItemSelected: Calendar selected");
            Intent intent = new Intent(this, FacultyCalendarActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.navigation_profile) {
            Log.d(TAG, "onNavigationItemSelected: Profile selected");
            Intent intent = new Intent(this, ProfileFacultyActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
        if (viewModel != null) {
            viewModel.loadDashboardData();
            viewModel.loadBookingsForCurrentWeek();
            viewModel.loadTodayBookings();
        }
    }
}
