package com.example.tluofficehours.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tluofficehours.model.AddScheduleData;
import com.example.tluofficehours.repository.FacultyRepository;
import com.example.tluofficehours.view.AddScheduleActivity;

import org.json.JSONObject;

public class AddScheduleViewModel extends AndroidViewModel {
    private static final String TAG = "AddScheduleViewModel";
    private final FacultyRepository facultyRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AddScheduleViewModel(@NonNull Application application) {
        super(application);
        facultyRepository = new FacultyRepository();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    private String formatTime(String time) {
        if (time == null) return "00:00:00";
        String[] parts = time.split(":");
        String hour = parts.length > 0 ? String.format("%02d", Integer.parseInt(parts[0])) : "00";
        String minute = parts.length > 1 ? String.format("%02d", Integer.parseInt(parts[1])) : "00";
        return hour + ":" + minute + ":00";
    }

    private String buildDateTimeUTC(String date, String time) {
        try {
            // date: yyyy-MM-dd, time: HH:mm hoặc HH:mm:ss
            String[] dateParts = date.split("-");
            String[] timeParts = time.split(":");
            java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
            cal.set(java.util.Calendar.YEAR, Integer.parseInt(dateParts[0]));
            cal.set(java.util.Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            cal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
            cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            cal.set(java.util.Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return date + " " + time + ":00";
        }
    }

    public void createSchedule(AddScheduleData scheduleData) {
        isLoading.setValue(true);
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("max_students", scheduleData.getMaxStudents());
        payload.put("slot_duration", scheduleData.getSlotDuration());
        payload.put("notes", scheduleData.getNotes());

        java.util.List<java.util.Map<String, String>> slots = new java.util.ArrayList<>();
        if (scheduleData.isSpecificDateMode()) {
            String date = scheduleData.getSelectedDate(); // yyyy-MM-dd
            if (scheduleData.getTimeSlots() != null) {
                for (com.example.tluofficehours.model.TimeSlot slot : scheduleData.getTimeSlots()) {
                    if (slot.isSelected()) {
                        java.util.Map<String, String> slotMap = new java.util.HashMap<>();
                        slotMap.put("start_time", buildDateTimeUTC(date, slot.getStartTime()));
                        slotMap.put("end_time", buildDateTimeUTC(date, slot.getEndTime()));
                        slots.add(slotMap);
                    }
                }
            }
        } else {
            // Lịch cố định: tạo slot cho từng ngày trong tháng hoặc tuần hiện tại
            int month = scheduleData.getMonth();
            int year = scheduleData.getYear();
            boolean applyMonthly = scheduleData.isApplyMonthly();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.YEAR, year);
            cal.set(java.util.Calendar.MONTH, month - 1); // Calendar.MONTH: 0-based
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            java.util.List<AddScheduleData.DayTimeSlot> dayTimeSlots = scheduleData.getDayTimeSlots();
            if (dayTimeSlots != null) {
                for (AddScheduleData.DayTimeSlot daySlot : dayTimeSlots) {
                    int dayOfWeek = daySlot.getDayOfWeek(); // 1=Monday ... 7=Sunday
                    if (daySlot.getTimeSlots() != null) {
                        if (applyMonthly) {
                            // Lặp qua từng ngày trong tháng, nếu đúng thứ thì tạo slot
                            for (int d = 1; d <= daysInMonth; d++) {
                                cal.set(java.util.Calendar.DAY_OF_MONTH, d);
                                int calDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                                // Java: SUNDAY=1, MONDAY=2,...,SATURDAY=7; app: MONDAY=1,...,SUNDAY=7
                                int appDayOfWeek = calDayOfWeek == 1 ? 7 : calDayOfWeek - 1;
                                if (appDayOfWeek == dayOfWeek) {
                                    String dateStr = String.format("%04d-%02d-%02d", year, month, d);
                                    for (com.example.tluofficehours.model.TimeSlot slot : daySlot.getTimeSlots()) {
                                        if (slot.isSelected()) {
                                            java.util.Map<String, String> slotMap = new java.util.HashMap<>();
                                            slotMap.put("start_time", buildDateTimeUTC(dateStr, slot.getStartTime()));
                                            slotMap.put("end_time", buildDateTimeUTC(dateStr, slot.getEndTime()));
                                            slots.add(slotMap);
                                        }
                                    }
                                }
                            }
                        } else {
                            // Chỉ tạo cho tuần hiện tại (giả sử tuần hiện tại là tuần chứa ngày đầu tháng)
                            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            int firstDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                            int offset = (dayOfWeek - (firstDayOfWeek == 1 ? 7 : firstDayOfWeek - 1) + 7) % 7;
                            int dayOfMonth = 1 + offset;
                            if (dayOfMonth <= daysInMonth) {
                                String dateStr = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
                                for (com.example.tluofficehours.model.TimeSlot slot : daySlot.getTimeSlots()) {
                                    if (slot.isSelected()) {
                                        java.util.Map<String, String> slotMap = new java.util.HashMap<>();
                                        slotMap.put("start_time", buildDateTimeUTC(dateStr, slot.getStartTime()));
                                        slotMap.put("end_time", buildDateTimeUTC(dateStr, slot.getEndTime()));
                                        slots.add(slotMap);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            payload.put("apply_monthly", applyMonthly);
            payload.put("month", month);
            payload.put("year", year);
        }
        payload.put("slots", slots);
        facultyRepository.createSchedule(payload).observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                successMessage.setValue("Tạo lịch thành công!");
            } else {
                errorMessage.setValue("Tạo lịch thất bại. Vui lòng thử lại!");
            }
        });
    }

    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
} 