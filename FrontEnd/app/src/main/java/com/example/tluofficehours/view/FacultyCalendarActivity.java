package com.example.tluofficehours.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.viewmodel.FacultyCalendarViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FacultyCalendarActivity extends AppCompatActivity {
    private static final String TAG = "FacultyCalendarActivity";

    private FacultyCalendarViewModel viewModel;
    private RecyclerView recyclerView;
    private FacultyCalendarAdapter adapter;
    private TextView tvCurrentWeekRange;
    private TextView tvCurrentDate;
    private ImageView btnPreviousWeek, btnNextWeek;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddSchedule;

    private List<TextView> dayTextViews = new ArrayList<>();
    private Button btnFilterAll, btnFilterPending, btnFilterConfirmed, btnFilterCompleted, btnFilterRejectedCancelled;
    private Button selectedFilterButton;

    private Calendar currentWeek;
    private Date selectedDate;
    private String currentFilterStatus = "ALL"; // Mặc định là "ALL"

    private LinearLayout emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_calendar);

        Log.d(TAG, "onCreate: Activity started.");

        initViews();
        setupViewModel();      // Khởi tạo ViewModel trước tiên
        setupRecyclerView();   // RecyclerView và Adapter cần ViewModel
        setupCalendar();       // Thiết lập Calendar cơ bản
        setupBottomNavigation();
        setupFilterButtons();  // Gọi này sẽ tự động loadInitialData

        // updateCalendarUI() sẽ được gọi từ setupFilterButtons (khi ALL được chọn)
        // và trong setupViewModel (khi observe). Không cần gọi ở đây nữa.
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewAppointments);
        tvCurrentWeekRange = findViewById(R.id.tvCurrentWeekRange);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        btnPreviousWeek = findViewById(R.id.btnPreviousWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        emptyStateView = findViewById(R.id.emptyState);
        fabAddSchedule = findViewById(R.id.fab_add_appointment);

        // Khởi tạo danh sách TextViews cho các ngày trong tuần
        dayTextViews.add(findViewById(R.id.tvDay0));
        dayTextViews.add(findViewById(R.id.tvDay1));
        dayTextViews.add(findViewById(R.id.tvDay2));
        dayTextViews.add(findViewById(R.id.tvDay3));
        dayTextViews.add(findViewById(R.id.tvDay4));
        dayTextViews.add(findViewById(R.id.tvDay5));
        dayTextViews.add(findViewById(R.id.tvDay6));

        // Đặt OnClickListener cho từng ngày trong tuần
        for (int i = 0; i < dayTextViews.size(); i++) {
            final int dayIndex = i;
            dayTextViews.get(i).setOnClickListener(v -> onDayClicked(dayIndex));
        }

        // Khởi tạo các nút lọc
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPending = findViewById(R.id.btnFilterPending);
        btnFilterConfirmed = findViewById(R.id.btnFilterConfirmed);
        btnFilterCompleted = findViewById(R.id.btnFilterCompleted);
        btnFilterRejectedCancelled = findViewById(R.id.btnFilterRejectedCancelled);

        // Đặt listener cho nút chuyển tuần
        btnPreviousWeek.setOnClickListener(v -> navigateToPreviousWeek());
        btnNextWeek.setOnClickListener(v -> navigateToNextWeek());

        // Đặt listener cho FAB
        fabAddSchedule.setOnClickListener(v -> openAddScheduleActivity());

        Log.d(TAG, "initViews: All views initialized.");
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);
        Log.d(TAG, "setupBottomNavigation: Bottom navigation set up.");
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            Intent intent = new Intent(this, FacultyMainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_calendar) {
            return true;
        } else if (itemId == R.id.navigation_profile) {
            Intent intent = new Intent(this, ProfileFacultyActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FacultyCalendarViewModel.class);
        Log.d(TAG, "setupViewModel: ViewModel initialized.");

        // Quan sát dữ liệu lịch hẹn đã được lọc từ ViewModel
        viewModel.getFilteredAppointments().observe(this, appointments -> {
            Log.d(TAG, "setupViewModel: Received " + (appointments != null ? appointments.size() : 0) + " filtered appointments.");
            if (adapter != null) {
                adapter.updateAppointments(appointments);
                // Ẩn/hiện empty state
                if (appointments == null || appointments.isEmpty()) {
                    emptyStateView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.d(TAG, "setupViewModel: Showing empty state.");
                } else {
                    emptyStateView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "setupViewModel: Showing appointments.");
                }
            } else {
                Log.e(TAG, "setupViewModel: Adapter is null when trying to update appointments.");
            }
        });

        // Quan sát trạng thái tải dữ liệu
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Hiển thị/ẩn thanh tiến trình tải dữ liệu ở đây nếu cần
            Log.d(TAG, "setupViewModel: Is loading: " + isLoading);
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "setupViewModel: Error: " + message);
                viewModel.clearErrorMessage();
            }
        });

        // Quan sát thông báo thành công
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "setupViewModel: Success: " + message);
                viewModel.clearSuccessMessage();
            }
        });

        // Observer mới để cập nhật UI lịch khi danh sách các ngày có sự kiện thay đổi
        viewModel.getEventDays().observe(this, eventDays -> {
            // eventDays là List<Date> mới
            Log.d(TAG, "Event days LiveData observed with " + (eventDays != null ? eventDays.size() : "null") + " events. Updating calendar UI.");
            updateCalendarUI(); // Gọi updateCalendarUI để vẽ lại lịch với dữ liệu mới nhất
        });
    }

    private void setupCalendar() {
        currentWeek = Calendar.getInstance(new Locale("vi", "VN"));
        currentWeek.setFirstDayOfWeek(Calendar.MONDAY); // Đặt Thứ Hai là ngày đầu tuần
        currentWeek.setTime(new Date()); // Đảm bảo tuần hiện tại là tuần của ngày hôm nay
        // Set selectedDate là ngày đầu tuần hiện tại
        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        selectedDate = tempCal.getTime();
        Log.d(TAG, "setupCalendar: Calendar initialized. currentWeek: " + currentWeek.getTime() + ", selectedDate: " + selectedDate);
        // updateCalendarUI() sẽ được gọi từ setupFilterButtons() sau khi khởi tạo ViewModel
    }

    private void setupRecyclerView() {
        adapter = new FacultyCalendarAdapter();
        
        // Set the ViewModel on the adapter after it's created
        if (viewModel != null) {
            adapter.setViewModel(viewModel);
            Log.d(TAG, "setupRecyclerView: ViewModel set on adapter.");
        } else {
            Log.e(TAG, "setupRecyclerView: ViewModel is null, cannot set on adapter.");
        }
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "setupRecyclerView: RecyclerView and Adapter set up.");

        // Đặt listener cho các sự kiện click trên item của RecyclerView
        adapter.setOnItemClickListener(new FacultyCalendarAdapter.OnItemClickListener() {
            @Override
            public void onDetailClick(Booking booking) {
                Log.d(TAG, "onDetailClick: Booking ID " + booking.getBookingId());
                // Dialog được xử lý trong adapter
            }

            @Override
            public void onApproveClick(Booking booking) {
                Log.d(TAG, "onApproveClick: Approving booking ID " + booking.getBookingId());
                if (viewModel != null) {
                    viewModel.approveBooking(booking.getBookingId(), selectedDate, currentFilterStatus);
                }
            }

            @Override
            public void onRejectClick(Booking booking) {
                Log.d(TAG, "onRejectClick: Rejecting booking ID " + booking.getBookingId());
                showReasonDialog("Từ chối", (reason) -> {
                    if (viewModel != null) {
                        viewModel.rejectBooking(booking.getBookingId(), reason, selectedDate, currentFilterStatus);
                    }
                });
            }

            @Override
            public void onCancelClick(Booking booking) {
                Log.d(TAG, "onCancelClick: Cancelling booking ID " + booking.getBookingId());
                showReasonDialog("Hủy", (reason) -> {
                    if (viewModel != null) {
                        viewModel.cancelBooking(booking.getBookingId(), reason, selectedDate, currentFilterStatus);
                    }
                });
            }

            @Override
            public void onCompleteClick(Booking booking) {
                Log.d(TAG, "onCompleteClick: Completing booking ID " + booking.getBookingId());
                if (viewModel != null) {
                    viewModel.markBookingCompleted(booking.getBookingId(), selectedDate, currentFilterStatus);
                }
            }
        });
    }

    private void navigateToPreviousWeek() {
        currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
        // Set selectedDate là ngày đầu tuần mới
        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        selectedDate = tempCal.getTime();
        updateCalendarUI();
        if (viewModel != null) {
            viewModel.loadAppointmentsForWeek(currentWeek.getTime(), currentFilterStatus);
            viewModel.setCurrentSelectedDate(selectedDate);
        }
        Log.d(TAG, "navigateToPreviousWeek: Moved to previous week. currentWeek: " + currentWeek.getTime());
    }

    private void navigateToNextWeek() {
        currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
        // Set selectedDate là ngày đầu tuần mới
        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        selectedDate = tempCal.getTime();
        if (viewModel != null) {
            viewModel.loadAppointmentsForWeek(currentWeek.getTime(), currentFilterStatus);
            viewModel.setCurrentSelectedDate(selectedDate);
        }
        updateCalendarUI();
        Log.d(TAG, "navigateToNextWeek: Navigated to week of " + currentWeek.getTime());
    }

    private void updateCalendarUI() {
        // Lấy danh sách các ngày có lịch hẹn từ ViewModel
        List<Date> eventDays = viewModel.getEventDays().getValue();
        if (eventDays == null) {
            eventDays = new ArrayList<>();
            Log.d(TAG, "updateCalendarUI: eventDays is null, initialized a new list.");
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat weekRangeFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = tempCal.getTime();
        tempCal.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = tempCal.getTime();

        tvCurrentWeekRange.setText(String.format("Tuần: %s - %s",
                weekRangeFormat.format(startDate),
                weekRangeFormat.format(endDate)));

        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            TextView dayView = dayTextViews.get(i);
            Date day = tempCal.getTime();
            dayView.setText(dayFormat.format(day));

            boolean isSelected = isSameDay(day, selectedDate);
            boolean hasEvent = false;
            for (Date eventDate : eventDays) {
                if (isSameDay(day, eventDate)) {
                    hasEvent = true;
                    break;
                }
            }

            if (hasEvent) {
                // Ưu tiên hiển thị màu xanh nếu ngày có lịch hẹn
                dayView.setBackgroundResource(R.drawable.calendar_day_event);
                dayView.setTextColor(Color.WHITE);
                dayView.setTypeface(null, isSelected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            } else if (isSelected) {
                // Chỉ hiển thị màu cam (đã chọn) nếu ngày không có lịch hẹn
                dayView.setBackgroundResource(R.drawable.calendar_day_selected);
                dayView.setTextColor(Color.WHITE);
                dayView.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                // Kiểu mặc định cho ngày không có lịch hẹn và không được chọn
                dayView.setBackgroundResource(R.drawable.calendar_day_default);
                dayView.setTextColor(Color.BLACK);
                dayView.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
            tempCal.add(Calendar.DAY_OF_WEEK, 1);
        }
        // Cập nhật ngày tháng hiển thị
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        tvCurrentDate.setText(sdf.format(selectedDate));
        Log.d(TAG, "updateCalendarUI: UI updated for week of " + currentWeek.getTime());
    }

    private void onDayClicked(int dayIndex) {
        Log.d(TAG, "onDayClicked: Day " + dayIndex + " clicked.");
        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        tempCal.add(Calendar.DATE, dayIndex);
        selectedDate = tempCal.getTime();

        updateCalendarUI();
        if (viewModel != null) {
            // Khi ngày thay đổi, tải lại dữ liệu với bộ lọc hiện tại
            viewModel.loadAppointmentsForWeek(currentWeek.getTime(), currentFilterStatus);
            // Đồng thời, kích hoạt lọc lại dựa trên selectedDate
            viewModel.setCurrentSelectedDate(selectedDate);
        }
    }

    private void setupFilterButtons() {
        // Đặt listener cho các nút lọc
        btnFilterAll.setOnClickListener(v -> selectFilterButton(btnFilterAll, "ALL"));
        btnFilterPending.setOnClickListener(v -> selectFilterButton(btnFilterPending, "pending"));
        btnFilterConfirmed.setOnClickListener(v -> selectFilterButton(btnFilterConfirmed, "confirmed"));
        btnFilterCompleted.setOnClickListener(v -> selectFilterButton(btnFilterCompleted, "completed"));
        btnFilterRejectedCancelled.setOnClickListener(v -> selectFilterButton(btnFilterRejectedCancelled, "REJECTED_CANCELLED"));

        // Set nút "Tất cả" là được chọn ban đầu và tải dữ liệu cho tuần hiện tại
        selectFilterButton(btnFilterAll, "ALL");
        Log.d(TAG, "setupFilterButtons: Filter buttons set up.");
    }

    private void selectFilterButton(Button button, String status) {
        Log.d(TAG, "selectFilterButton: Selected filter: " + status);

        // Bỏ chọn nút cũ (nếu có)
        if (selectedFilterButton != null) {
            selectedFilterButton.setSelected(false);
            // Reset background và text color của nút cũ về trạng thái không được chọn
            selectedFilterButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));
            selectedFilterButton.setTextColor(getResources().getColor(R.color.colorPrimary)); // Màu mặc định cho text
        }

        // Chọn nút mới
        button.setSelected(true);
        selectedFilterButton = button;
        // Cập nhật background và text color của nút mới thành trạng thái được chọn
        // Bạn cần định nghĩa colorPrimary trong colors.xml hoặc dùng màu cố định
        selectedFilterButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        selectedFilterButton.setTextColor(getResources().getColor(android.R.color.white));

        currentFilterStatus = status; // Cập nhật trạng thái lọc hiện tại
        if (viewModel != null) {
            // Khi bộ lọc thay đổi, tải lại dữ liệu cho tuần hiện tại và kích hoạt lọc theo ngày/trạng thái
            viewModel.loadAppointmentsForWeek(currentWeek.getTime(), currentFilterStatus);
            viewModel.setCurrentSelectedDate(selectedDate); // Kích hoạt lọc lại bằng ngày đã chọn
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void showReasonDialog(String action, ReasonDialogCallback callback) {
        Log.d(TAG, "showReasonDialog: Showing reason dialog for action: " + action);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Lý do " + action.toLowerCase());

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nhập lý do...");
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (!reason.isEmpty()) {
                callback.onReasonEntered(reason);
                Log.d(TAG, "showReasonDialog: Reason entered: " + reason);
            } else {
                Toast.makeText(this, "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "showReasonDialog: Reason is empty.");
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.cancel();
            Log.d(TAG, "showReasonDialog: Dialog cancelled.");
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            Log.d(TAG, "onBackPressed: Popped fragment from back stack.");
        } else {
            super.onBackPressed();
            Log.d(TAG, "onBackPressed: Exiting activity.");
        }
    }

    private void openAddScheduleActivity() {
        Intent intent = new Intent(this, AddScheduleActivity.class);
        startActivity(intent);
    }

    interface ReasonDialogCallback {
        void onReasonEntered(String reason);
    }
}