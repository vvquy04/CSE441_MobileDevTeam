package com.example.tluofficehours.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.AddScheduleData;
import com.example.tluofficehours.model.TimeSlot;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.example.tluofficehours.viewmodel.AddScheduleViewModel;
import android.widget.ProgressBar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

public class AddScheduleActivity extends AppCompatActivity implements TimeSlotAdapter.OnTimeSlotActionListener {

    private static final String TAG = "AddScheduleActivity";

    // UI Components for Fixed Schedule Mode
    private ImageButton btnBack;
    private Button btnSave;
    private MaterialButtonToggleGroup toggleGroup;
    private CardView cardSpecificDate;
    private LinearLayout fixedScheduleContainer;
    private RadioGroup rgSlotDuration;
    private Switch switchGroupBooking;
    private Spinner spinnerMaxStudents;
    private EditText etNotes;

    // UI Components for Specific Date Mode
    private RecyclerView rvTimeSlotsSpecific;
    private MaterialButton btnAddTimeSlotSpecific;
    private RadioGroup rgSlotDurationSpecific;
    private Switch switchGroupBookingSpecific;
    private Spinner spinnerMaxStudentsSpecific;
    private EditText etNotesSpecific;

    // Calendar components
    private TextView tvSelectedDate;
    private LinearLayout calendarContainer;
    private TextView tvCurrentWeekRange;
    private ImageView btnPreviousWeek, btnNextWeek;
    private final List<TextView> dayTextViews = new ArrayList<>();

    // Monthly schedule components
    private Switch switchMonthlySchedule;
    private TextView tvMonthlyPreview;

    // Data
    private TimeSlotAdapter timeSlotAdapterSpecific; // Adapter cho chế độ ngày cụ thể
    private boolean isSpecificDateMode = false;
    private Date selectedDate;
    private Calendar currentWeek;

    private AddScheduleViewModel viewModel;
    private ProgressBar progressBar;

    // Individual day containers and checkboxes
    private List<LinearLayout> dayContainers = new ArrayList<>();
    private List<CheckBox> dayCheckBoxes = new ArrayList<>();
    private List<LinearLayout> dayTimeSlotsContainers = new ArrayList<>();
    private List<RecyclerView> dayRecyclerViews = new ArrayList<>();
    private List<MaterialButton> dayAddButtons = new ArrayList<>();
    private List<TimeSlotAdapter> dayTimeSlotAdapters = new ArrayList<>();

    private LinearLayout layoutSpecificDate;

    // Thêm biến cho chọn tháng
    private TextView tvSelectedMonth;
    private ImageButton btnPickMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(AddScheduleViewModel.class);

        Log.d(TAG, "onCreate: Activity started.");

        initViews();
        setupRecyclerView();
        setupListeners();
        setupSpinner();
        setupCalendar();
        setupDefaultState();
        setupObservers();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        toggleGroup = findViewById(R.id.toggle_group);
        fixedScheduleContainer = findViewById(R.id.fixed_schedule_container);
        rgSlotDuration = findViewById(R.id.rg_slot_duration);
        switchGroupBooking = findViewById(R.id.switch_group_booking);
        spinnerMaxStudents = findViewById(R.id.spinner_max_students);
        etNotes = findViewById(R.id.et_notes);

        // Specific date mode views
        rvTimeSlotsSpecific = findViewById(R.id.rv_time_slots_specific);
        btnAddTimeSlotSpecific = findViewById(R.id.btn_add_time_slot_specific);
        rgSlotDurationSpecific = findViewById(R.id.rg_slot_duration_specific);
        switchGroupBookingSpecific = findViewById(R.id.switch_group_booking_specific);
        spinnerMaxStudentsSpecific = findViewById(R.id.spinner_max_students_specific);
        etNotesSpecific = findViewById(R.id.et_notes_specific);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        calendarContainer = findViewById(R.id.calendar_container);
        tvCurrentWeekRange = findViewById(R.id.tvCurrentWeekRange);
        btnPreviousWeek = findViewById(R.id.btnPreviousWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);

        // Monthly schedule toggle
        switchMonthlySchedule = findViewById(R.id.switch_apply_monthly);
        tvMonthlyPreview = findViewById(R.id.tv_monthly_preview);

        // Initialize day views
        initDayViews();

        // Initialize calendar day text views
        for (int i = 0; i < 7; i++) {
            int dayTextViewId = getResources().getIdentifier("tvDay" + i, "id", getPackageName());
            TextView dayTextView = findViewById(dayTextViewId);
            dayTextViews.add(dayTextView);
        }

        progressBar = findViewById(R.id.progressBar);

        tvSelectedDate.setOnClickListener(v -> showMaterialDatePicker());

        layoutSpecificDate = findViewById(R.id.layout_specific_date);

        // Thêm biến cho chọn tháng
        tvSelectedMonth = findViewById(R.id.tv_selected_month);
        btnPickMonth = findViewById(R.id.btn_pick_month);
    }

    private void initDayViews() {
        // Initialize day containers
        for (int i = 0; i < 7; i++) {
            int containerId = getResources().getIdentifier("day_" + i + "_container", "id", getPackageName());
            LinearLayout container = findViewById(containerId);
            dayContainers.add(container);

            // Initialize checkbox
            int checkboxId = getResources().getIdentifier("cb_day_" + i, "id", getPackageName());
            CheckBox checkbox = findViewById(checkboxId);
            dayCheckBoxes.add(checkbox);

            // Initialize time slots container
            int timeSlotsContainerId = getResources().getIdentifier("day_" + i + "_time_slots_container", "id", getPackageName());
            LinearLayout timeSlotsContainer = findViewById(timeSlotsContainerId);
            dayTimeSlotsContainers.add(timeSlotsContainer);

            // Initialize RecyclerView
            int recyclerViewId = getResources().getIdentifier("rv_time_slots_day_" + i, "id", getPackageName());
            RecyclerView recyclerView = findViewById(recyclerViewId);
            dayRecyclerViews.add(recyclerView);

            // Initialize add button
            int addButtonId = getResources().getIdentifier("btn_add_time_slot_day_" + i, "id", getPackageName());
            MaterialButton addButton = findViewById(addButtonId);
            dayAddButtons.add(addButton);

            // Initialize TimeSlotAdapter for this day
            TimeSlotAdapter adapter = new TimeSlotAdapter(this);
            adapter.setOnTimeSlotActionListener(this);
            dayTimeSlotAdapters.add(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_specific_date) {
                    switchToSpecificDateMode();
                } else if (checkedId == R.id.btn_fixed_schedule) {
                    switchToFixedScheduleMode();
                }
            }
        });

        btnAddTimeSlotSpecific.setOnClickListener(v -> addNewTimeSlotSpecific());

        // Setup individual day listeners
        setupDayListeners();

        // Calendar navigation
        btnPreviousWeek.setOnClickListener(v -> navigateToPreviousWeek());
        btnNextWeek.setOnClickListener(v -> navigateToNextWeek());

        // Monthly schedule toggle
        switchMonthlySchedule.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateMonthlyPreview(isChecked);
        });

        // Slot duration preview
        rgSlotDuration.setOnCheckedChangeListener((group, checkedId) -> {
            updateSlotDurationPreview(false);
        });

        rgSlotDurationSpecific.setOnCheckedChangeListener((group, checkedId) -> {
            updateSlotDurationPreview(true);
        });

        // Group booking settings
        switchGroupBooking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateGroupBookingSettings(isChecked, false);
        });

        switchGroupBookingSpecific.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateGroupBookingSettings(isChecked, true);
        });

        btnSave.setOnClickListener(v -> showConfirmationDialog());

        btnPickMonth.setOnClickListener(v -> showMonthPickerDialog());
    }

    private void setupDayListeners() {
        for (int i = 0; i < 7; i++) {
            final int dayIndex = i;
            
            // Setup checkbox listener
            dayCheckBoxes.get(i).setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    dayTimeSlotsContainers.get(dayIndex).setVisibility(View.VISIBLE);
                } else {
                    dayTimeSlotsContainers.get(dayIndex).setVisibility(View.GONE);
                }
                updateMonthlyPreview(switchMonthlySchedule.isChecked());
                updateSaveButtonState();
            });

            // Setup add button listener
            dayAddButtons.get(i).setOnClickListener(v -> addNewTimeSlotForDay(dayIndex));
        }
    }

    private void setupRecyclerView() {
        // Adapter cho chế độ ngày cụ thể
        timeSlotAdapterSpecific = new TimeSlotAdapter(this);
        timeSlotAdapterSpecific.setOnTimeSlotActionListener(this);
        rvTimeSlotsSpecific.setLayoutManager(new LinearLayoutManager(this));
        rvTimeSlotsSpecific.setAdapter(timeSlotAdapterSpecific);

        Log.d(TAG, "setupRecyclerView: RecyclerView and adapters set up.");
    }

    private void setupSpinner() {
        String[] maxStudentsOptions = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, maxStudentsOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Setup spinner cho chế độ lịch cố định
        spinnerMaxStudents.setAdapter(adapter);
        spinnerMaxStudents.setSelection(4); // Default to "5"
        
        // Setup spinner cho chế độ ngày cụ thể
        spinnerMaxStudentsSpecific.setAdapter(adapter);
        spinnerMaxStudentsSpecific.setSelection(4); // Default to "5"
        
        Log.d(TAG, "setupSpinner: Spinners for max students set up (1-5).");
    }

    private void setupCalendar() {
        // Weekly calendar setup
        currentWeek = Calendar.getInstance(new Locale("vi", "VN"));
        currentWeek.setFirstDayOfWeek(Calendar.MONDAY);
        currentWeek.setTime(new Date());
        updateCalendarUI();
        Log.d(TAG, "setupCalendar: Weekly calendar initialized.");

        // Monthly calendar setup (dùng MaterialDatePicker)
        selectedDate = new Date();
        updateSelectedDateText();
        updateSelectedMonthText();
    }

    private void setupDefaultState() {
        toggleGroup.check(R.id.btn_fixed_schedule);
        switchToFixedScheduleMode();
        
        // Setup default cho chế độ lịch cố định
        ((MaterialRadioButton) findViewById(R.id.rb_20_min)).setChecked(true);
        switchGroupBooking.setChecked(true);
        updateGroupBookingSettings(true, false);
        
        // Setup default cho chế độ ngày cụ thể
        ((MaterialRadioButton) findViewById(R.id.rb_20_min_specific)).setChecked(true);
        switchGroupBookingSpecific.setChecked(true);
        updateGroupBookingSettings(true, true);
        
        Log.d(TAG, "setupDefaultState: Default UI state set.");
    }

    private void switchToSpecificDateMode() {
        isSpecificDateMode = true;
        layoutSpecificDate.setVisibility(View.VISIBLE);
        fixedScheduleContainer.setVisibility(View.GONE);
        updateSelectedDateText();
    }

    private void switchToFixedScheduleMode() {
        isSpecificDateMode = false;
        layoutSpecificDate.setVisibility(View.GONE);
        fixedScheduleContainer.setVisibility(View.VISIBLE);
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày")
                .setSelection(selectedDate != null ? selectedDate.getTime() : MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            updateSelectedDateText();
            Toast.makeText(this, "Đã chọn ngày: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate), Toast.LENGTH_SHORT).show();
        });
        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void updateSelectedDateText() {
        if (tvSelectedDate != null && selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvSelectedDate.setText(sdf.format(selectedDate));
        }
    }

    private void updateCalendarUI() {
        if (currentWeek == null) {
            Log.w(TAG, "updateCalendarUI: currentWeek is null, cannot update UI.");
            return;
        }

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
        tvCurrentWeekRange.setText(monthYearFormat.format(currentWeek.getTime()));
        Log.d(TAG, "updateCalendarUI: Current week range text updated to: " + tvCurrentWeekRange.getText());

        Calendar tempCal = (Calendar) currentWeek.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < dayTextViews.size(); i++) {
            TextView dayView = dayTextViews.get(i);
            if (dayView == null) {
                Log.w(TAG, "updateCalendarUI: dayView at index " + i + " is null, skipping.");
                continue;
            }

            Date dateForDay = tempCal.getTime();
            dayView.setText(String.valueOf(tempCal.get(Calendar.DAY_OF_MONTH)));

            if (selectedDate != null && isSameDay(dateForDay, selectedDate)) {
                dayView.setBackgroundResource(R.drawable.calendar_day_event);
                dayView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                Log.d(TAG, "updateCalendarUI: Highlighted day: " + dayView.getText());
            } else {
                dayView.setBackgroundResource(R.drawable.calendar_day_default);
                dayView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
            tempCal.add(Calendar.DATE, 1);
        }
        Log.d(TAG, "updateCalendarUI: Weekly calendar days updated.");

        updateSelectedMonthText();
    }

    private void navigateToPreviousWeek() {
        currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
        updateCalendarUI();
        Log.d(TAG, "navigateToPreviousWeek: Moved to previous week. Current week: " + currentWeek.getTime());
    }

    private void navigateToNextWeek() {
        currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
        updateCalendarUI();
        Log.d(TAG, "navigateToNextWeek: Moved to next week. Current week: " + currentWeek.getTime());
    }

    private void onDayClicked(int dayIndex) {
        // Toggle selection for the clicked day
        CheckBox checkbox = dayCheckBoxes.get(dayIndex);
        checkbox.setChecked(!checkbox.isChecked());
        
        // Update visibility of time slots container
        LinearLayout timeSlotsContainer = dayTimeSlotsContainers.get(dayIndex);
        if (checkbox.isChecked()) {
            timeSlotsContainer.setVisibility(View.VISIBLE);
        } else {
            timeSlotsContainer.setVisibility(View.GONE);
        }
        
        updateMonthlyPreview(switchMonthlySchedule.isChecked());
        updateSaveButtonState();
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void addNewTimeSlotSpecific() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog startTimePickerDialog = new TimePickerDialog(this, (view, startHour, startMinute) -> {
            String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

            TimePickerDialog endTimePickerDialog = new TimePickerDialog(this, (endView, endHour, endMinute) -> {
                String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

                if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                    Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
                    return;
                }
                TimeSlot newTimeSlot = new TimeSlot(startTime, endTime);
                timeSlotAdapterSpecific.addTimeSlot(newTimeSlot);
                Toast.makeText(this, "Đã thêm khung giờ mới", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "addNewTimeSlot: Added new time slot: " + startTime + " - " + endTime);

            }, hour, minute, true);
            endTimePickerDialog.setTitle("Chọn giờ kết thúc");
            endTimePickerDialog.show();

        }, hour, minute, true);
        startTimePickerDialog.setTitle("Chọn giờ bắt đầu");
        startTimePickerDialog.show();
    }

    private void updateSlotDurationPreview(boolean isSpecificDateMode) {
        Log.d(TAG, "updateSlotDurationPreview: Slot duration preview updated.");
    }

    private void updateGroupBookingSettings(boolean isEnabled, boolean isSpecificDateMode) {
        if (isSpecificDateMode) {
            // Logic cho chế độ ngày cụ thể
            if (isEnabled) {
                spinnerMaxStudentsSpecific.setEnabled(true);
            } else {
                spinnerMaxStudentsSpecific.setEnabled(false);
                spinnerMaxStudentsSpecific.setSelection(0); // Reset về 1
            }
        } else {
            // Logic cho chế độ lịch cố định
            if (isEnabled) {
                spinnerMaxStudents.setEnabled(true);
            } else {
                spinnerMaxStudents.setEnabled(false);
                spinnerMaxStudents.setSelection(0); // Reset về 1
            }
        }
    }

    private void updateMonthlyPreview(boolean isMonthlyEnabled) {
        if (isMonthlyEnabled) {
            // Tính số tuần còn lại trong tháng
            int remainingWeeks = calculateRemainingWeeksInMonth();
            
            // Lấy pattern các ngày được chọn
            String patternDays = getSelectedDaysPattern();
            
            if (patternDays.isEmpty()) {
                tvMonthlyPreview.setText("Vui lòng chọn ít nhất một ngày trong tuần");
                tvMonthlyPreview.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvMonthlyPreview.setText("Sẽ tạo lịch cho " + remainingWeeks + " tuần tiếp theo\nPattern: " + patternDays);
                tvMonthlyPreview.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            }
        } else {
            tvMonthlyPreview.setText("Chỉ tạo lịch cho tuần hiện tại");
            tvMonthlyPreview.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private String getSelectedDaysPattern() {
        Set<Integer> selectedDays = new HashSet<>();
        String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        
        for (int i = 0; i < dayCheckBoxes.size(); i++) {
            if (dayCheckBoxes.get(i).isChecked()) {
                selectedDays.add(i);
            }
        }

        if (selectedDays.isEmpty()) {
            return "";
        }

        List<String> selectedDayNames = new ArrayList<>();
        for (Integer dayIndex : selectedDays) {
            if (dayIndex >= 0 && dayIndex < dayNames.length) {
                selectedDayNames.add(dayNames[dayIndex]);
            }
        }

        return String.join(", ", selectedDayNames);
    }

    private int calculateRemainingWeeksInMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeek.getTime());
        
        // Lấy tuần đầu tiên của tháng
        Calendar firstWeekOfMonth = (Calendar) calendar.clone();
        firstWeekOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        firstWeekOfMonth.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        
        // Lấy tuần cuối cùng của tháng
        Calendar lastWeekOfMonth = (Calendar) calendar.clone();
        lastWeekOfMonth.set(Calendar.DAY_OF_MONTH, lastWeekOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastWeekOfMonth.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        
        // Tính số tuần trong tháng
        int totalWeeksInMonth = lastWeekOfMonth.get(Calendar.WEEK_OF_MONTH);
        
        // Tính tuần hiện tại trong tháng
        int currentWeekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        
        // Trả về số tuần còn lại
        return Math.max(0, totalWeeksInMonth - currentWeekInMonth + 1);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_save, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            performSaveSchedule();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnSave.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                // Thu thập lại dữ liệu để gửi sang màn hình success
                AddScheduleData scheduleData = collectScheduleData();
                Intent intent = new Intent(this, CreateScheduleSuccessActivity.class);
                intent.putExtra("SCHEDULE_DATA", scheduleData);
                startActivity(intent);
                finish();
                viewModel.clearMessages();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
                viewModel.clearMessages();
            }
        });
    }

    private void performSaveSchedule() {
        if (!validateInput()) {
            Log.w(TAG, "performSaveSchedule: Input validation failed.");
            return;
        }

        AddScheduleData scheduleData = collectScheduleData();
        Log.d("SCHEDULE_DATA", new com.google.gson.Gson().toJson(scheduleData));
        if (!isSpecificDateMode) {
            Log.d("SLOT_MAU", new com.google.gson.Gson().toJson(scheduleData.getDayTimeSlots()));
        }
        Log.d(TAG, "performSaveSchedule: Data collected, calling ViewModel to create schedule.");
        viewModel.createSchedule(scheduleData);
    }

    private boolean validateInput() {
        if (isSpecificDateMode) {
            // Kiểm tra cho chế độ ngày cụ thể
            if (selectedDate == null) {
                Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (timeSlotAdapterSpecific == null || timeSlotAdapterSpecific.getTimeSlots() == null || timeSlotAdapterSpecific.getTimeSlots().isEmpty()) {
                Toast.makeText(this, "Vui lòng thêm ít nhất một khung giờ", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            boolean hasSelectedSlots = false;
            for (TimeSlot timeSlot : timeSlotAdapterSpecific.getTimeSlots()) {
                if (timeSlot.isSelected()) {
                    hasSelectedSlots = true;
                    if (!timeSlot.isValid()) {
                        Toast.makeText(this, "Có khung giờ không hợp lệ: " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            
            if (!hasSelectedSlots) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một khung giờ", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            // Kiểm tra cho chế độ lịch cố định - kiểm tra các ngày được chọn
            boolean hasSelectedDays = false;
            String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
            
            for (int i = 0; i < dayCheckBoxes.size(); i++) {
                if (dayCheckBoxes.get(i).isChecked()) {
                    hasSelectedDays = true;
                    TimeSlotAdapter adapter = dayTimeSlotAdapters.get(i);
                    
                    if (adapter.getTimeSlots() == null || adapter.getTimeSlots().isEmpty()) {
                        Toast.makeText(this, "Vui lòng thêm ít nhất một khung giờ cho " + dayNames[i], Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    
                    // Kiểm tra tính hợp lệ của các time slots
                    for (TimeSlot timeSlot : adapter.getTimeSlots()) {
                        if (!timeSlot.isValid()) {
                            Toast.makeText(this, "Có khung giờ không hợp lệ trong " + dayNames[i] + ": " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime(), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
            }
            
            if (!hasSelectedDays) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ngày trong tuần", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }

    private AddScheduleData collectScheduleData() {
        AddScheduleData data = new AddScheduleData();
        data.setSpecificDateMode(isSpecificDateMode);
        
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            data.setSelectedDate(sdf.format(selectedDate));
        }
        
        // Lấy time slots từ adapter tương ứng
        if (isSpecificDateMode) {
            if (timeSlotAdapterSpecific != null) {
                data.setTimeSlots(timeSlotAdapterSpecific.getTimeSlots());
            } else {
                data.setTimeSlots(new ArrayList<>());
            }
            data.setNotes(etNotesSpecific.getText().toString().trim());
            
            // Lấy cấu hình slot từ chế độ ngày cụ thể
            int checkedId = rgSlotDurationSpecific.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_15_min_specific) {
                data.setSlotDuration(15);
            } else if (checkedId == R.id.rb_30_min_specific) {
                data.setSlotDuration(30);
            } else {
                data.setSlotDuration(20);
            }
            
            data.setGroupBookingEnabled(switchGroupBookingSpecific.isChecked());
            data.setMaxStudents(spinnerMaxStudentsSpecific.getSelectedItemPosition() + 1);
            data.setApplyMonthly(switchMonthlySchedule.isChecked());
        } else {
            // Thu thập time slots từ tất cả các ngày được chọn, CHỈ LẤY SLOT ĐƯỢC CHỌN
            List<AddScheduleData.DayTimeSlot> dayTimeSlots = new ArrayList<>();
            for (int i = 0; i < dayCheckBoxes.size(); i++) {
                if (dayCheckBoxes.get(i).isChecked()) {
                    TimeSlotAdapter adapter = dayTimeSlotAdapters.get(i);
                    List<TimeSlot> selectedSlots = new ArrayList<>();
                    for (TimeSlot slot : adapter.getTimeSlots()) {
                        if (slot.isSelected()) {
                            selectedSlots.add(slot);
                        }
                    }
                    if (!selectedSlots.isEmpty()) {
                        int dayOfWeek = i + 1;
                        AddScheduleData.DayTimeSlot dayTimeSlot = new AddScheduleData.DayTimeSlot(dayOfWeek, selectedSlots);
                        dayTimeSlots.add(dayTimeSlot);
                    }
                }
            }
            data.setDayTimeSlots(dayTimeSlots);
            data.setNotes(etNotes.getText().toString().trim());
            
            // Lấy cấu hình slot từ chế độ lịch cố định
            int checkedId = rgSlotDuration.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_15_min) {
                data.setSlotDuration(15);
            } else if (checkedId == R.id.rb_30_min) {
                data.setSlotDuration(30);
            } else {
                data.setSlotDuration(20);
            }
            
            data.setGroupBookingEnabled(switchGroupBooking.isChecked());
            data.setMaxStudents(spinnerMaxStudents.getSelectedItemPosition() + 1);
            data.setApplyMonthly(switchMonthlySchedule.isChecked());
            // Set tháng/năm từ currentWeek (tháng: 1-12)
            if (currentWeek != null) {
                data.setMonth(currentWeek.get(Calendar.MONTH) + 1);
                data.setYear(currentWeek.get(Calendar.YEAR));
            }
        }
        
        return data;
    }

    @Override
    public void onTimeSlotDelete(int position) {
        Toast.makeText(this, "Đã xóa khung giờ", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTimeSlotDelete: Time slot at position " + position + " deleted.");
    }

    @Override
    public void onTimeSlotCheck(int position, boolean isChecked) {
        Log.d(TAG, "onTimeSlotCheck: Time slot at position " + position + " checked: " + isChecked);
        updateSaveButtonState();
    }

    @Override
    public void onTimeChanged(int position, TimeSlot timeSlot) {
        Log.d(TAG, "onTimeChanged: Time slot at position " + position + " time changed to: " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime());
    }

    @Override
    public void onTimeSlotSelectionChanged() {
        // Cập nhật preview cho monthly schedule (chỉ cho chế độ lịch cố định)
        if (!isSpecificDateMode) {
            updateMonthlyPreview(switchMonthlySchedule.isChecked());
        }
        
        // Cập nhật trạng thái nút Save
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        boolean hasValidData = false;
        
        if (isSpecificDateMode) {
            // Kiểm tra cho chế độ ngày cụ thể
            if (timeSlotAdapterSpecific != null && timeSlotAdapterSpecific.getTimeSlots() != null) {
                boolean hasSelectedSlots = false;
                for (TimeSlot slot : timeSlotAdapterSpecific.getTimeSlots()) {
                    if (slot.isSelected()) {
                        hasSelectedSlots = true;
                        break;
                    }
                }
                
                if (hasSelectedSlots && selectedDate != null) {
                    hasValidData = true;
                }
            }
        } else {
            // Kiểm tra cho chế độ lịch cố định - kiểm tra các ngày được chọn
            boolean hasSelectedDays = false;
            boolean allSelectedDaysHaveSlots = true;
            
            for (int i = 0; i < dayCheckBoxes.size(); i++) {
                if (dayCheckBoxes.get(i).isChecked()) {
                    hasSelectedDays = true;
                    TimeSlotAdapter adapter = dayTimeSlotAdapters.get(i);
                    
                    if (adapter.getTimeSlots() == null || adapter.getTimeSlots().isEmpty()) {
                        allSelectedDaysHaveSlots = false;
                        break;
                    }
                }
            }
            
            hasValidData = hasSelectedDays && allSelectedDaysHaveSlots;
        }
        
        btnSave.setEnabled(hasValidData);
    }

    private void addNewTimeSlotForDay(int dayIndex) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog startTimePickerDialog = new TimePickerDialog(this, (view, startHour, startMinute) -> {
            String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

            TimePickerDialog endTimePickerDialog = new TimePickerDialog(this, (endView, endHour, endMinute) -> {
                String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

                if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                    Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                TimeSlot newTimeSlot = new TimeSlot(startTime, endTime);
                dayTimeSlotAdapters.get(dayIndex).addTimeSlot(newTimeSlot);
                Toast.makeText(this, "Đã thêm khung giờ mới", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "addNewTimeSlotForDay: Added time slot for day " + dayIndex + ": " + startTime + " - " + endTime);

            }, hour, minute, true);
            endTimePickerDialog.setTitle("Chọn giờ kết thúc");
            endTimePickerDialog.show();

        }, hour, minute, true);
        startTimePickerDialog.setTitle("Chọn giờ bắt đầu");
        startTimePickerDialog.show();
    }

    private void updateSelectedMonthText() {
        if (tvSelectedMonth != null && currentWeek != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            tvSelectedMonth.setText("Tháng " + sdf.format(currentWeek.getTime()));
        }
    }

    // Hàm mở MaterialDatePicker chọn tháng
    private void showMonthPickerDialog() {
        // Sử dụng MaterialDatePicker dạng chọn tháng (nếu dùng thư viện mới)
        // Nếu chưa có, dùng DatePickerDialog và chỉ lấy tháng/năm
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentWeek.getTime());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, m);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            currentWeek = (Calendar) cal.clone();
            updateCalendarUI();
        }, year, month, 1);
        // Ẩn chọn ngày
        try {
            java.lang.reflect.Field[] datePickerFields = dialog.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerField : datePickerFields) {
                if ("mDatePicker".equals(datePickerField.getName())) {
                    datePickerField.setAccessible(true);
                    Object datePicker = datePickerField.get(dialog);
                    java.lang.reflect.Field[] fields = datePicker.getClass().getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        if ("mDaySpinner".equals(field.getName())) {
                            field.setAccessible(true);
                            Object dayPicker = field.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Không cần xử lý, chỉ là ẩn ngày
        }
        dialog.setTitle("Chọn tháng/năm");
        dialog.show();
    }
}