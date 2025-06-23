package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tluofficehours.adapter.AppointmentAdapter;
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.model.Appointment;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingAppointmentsFragment extends Fragment {
    private static final String TAG = "UpcomingFragment";
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private AppointmentAdapter adapter;
    private List<Appointment> allAppointments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_appointments, container, false);
        
        // Tạo RecyclerView
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Tạo empty state text
        emptyStateText = new TextView(requireContext());
        emptyStateText.setText("Không có lịch hẹn sắp tới");
        emptyStateText.setTextSize(16);
        emptyStateText.setGravity(android.view.Gravity.CENTER);
        emptyStateText.setPadding(0, 100, 0, 0);
        
        adapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onCancel(Appointment appointment, String reason) {
                cancelAppointment(appointment, reason);
            }
            @Override
            public void onRebook(Appointment appointment) {
                Toast.makeText(requireContext(), "Chức năng đặt lại lịch sẽ được bổ sung!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onViewDetails(Appointment appointment) {
                // Mở AppointmentDetailActivity với dữ liệu từ appointment
                Intent intent = new Intent(requireContext(), AppointmentDetailActivity.class);
                intent.putExtra("teacherName", appointment.getTeacherName());
                intent.putExtra("department", appointment.getDepartment());
                intent.putExtra("date", appointment.getDate());
                intent.putExtra("time", appointment.getTime());
                intent.putExtra("purpose", appointment.getPurpose());
                intent.putExtra("room", appointment.getRoom());
                intent.putExtra("appointmentId", appointment.getId());
                intent.putExtra("avatarUrl", appointment.getAvatarUrl());
                intent.putExtra("status", appointment.getStatus());
                intent.putExtra("cancellationReason", appointment.getCancellationReason());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        
        // Thay thế layout cũ bằng RecyclerView và empty state
        ViewGroup root = (ViewGroup) view;
        root.removeAllViews();
        root.addView(recyclerView);
        root.addView(emptyStateText);
        
        fetchAppointments();
        return view;
    }

    private void fetchAppointments() {
        Log.d(TAG, "Fetching appointments...");
        ApiService apiService = com.example.tluofficehours.api.RetrofitClient.getApiService();
        apiService.getStudentAppointments().enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                Log.d(TAG, "Response received: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    allAppointments = response.body();
                    Log.d(TAG, "Total appointments: " + allAppointments.size());
                    
                    List<Appointment> upcoming = new ArrayList<>();
                    for (Appointment appt : allAppointments) {
                        Log.d(TAG, "Appointment: " + appt.getTeacherName() + " - Status: " + appt.getStatus());
                        if ("CONFIRMED".equals(appt.getStatus()) || "PENDING".equals(appt.getStatus()) || "BOOKED".equals(appt.getStatus())) {
                            upcoming.add(appt);
                        }
                    }
                    Log.d(TAG, "Upcoming appointments: " + upcoming.size());
                    
                    adapter.setAppointments(upcoming);
                    
                    // Hiển thị/ẩn empty state
                    if (upcoming.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateText.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateText.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    Toast.makeText(requireContext(), "Không thể tải lịch hẹn!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment(Appointment appointment, String reason) {
        ApiService apiService = com.example.tluofficehours.api.RetrofitClient.getApiService();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("reason", reason);
        apiService.cancelAppointment(appointment.getId(), body).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                    fetchAppointments();
                } else {
                    Toast.makeText(requireContext(), "Hủy lịch thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}