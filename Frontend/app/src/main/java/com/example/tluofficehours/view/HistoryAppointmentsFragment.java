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

public class HistoryAppointmentsFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private AppointmentAdapter adapter;
    private List<Appointment> allAppointments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_apppointments, container, false);
        
        // Tạo RecyclerView
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Tạo empty state text
        emptyStateText = new TextView(requireContext());
        emptyStateText.setText("Không có lịch hẹn trong lịch sử");
        emptyStateText.setTextSize(16);
        emptyStateText.setGravity(android.view.Gravity.CENTER);
        emptyStateText.setPadding(0, 100, 0, 0);
        
        adapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onCancel(Appointment appointment, String reason) {
                Toast.makeText(requireContext(), "Không thể hủy lịch đã hoàn thành hoặc đã hủy!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRebook(Appointment appointment) {
                Intent intent = new Intent(requireContext(), FacultyDetailActivity.class);
                intent.putExtra("facultyUserId", appointment.getFacultyUserId());
                startActivity(intent);
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
                    
                    List<Appointment> history = new ArrayList<>();
                    for (Appointment appt : allAppointments) {
                        Log.d(TAG, "Appointment: " + appt.getTeacherName() + " - Status: " + appt.getStatus());
                        if ("COMPLETED".equals(appt.getStatus()) || "CANCELLED".equals(appt.getStatus())) {
                            history.add(appt);
                        }
                    }
                    Log.d(TAG, "History appointments: " + history.size());
                    
                    adapter.setAppointments(history);
                    
                    // Hiển thị/ẩn empty state
                    if (history.isEmpty()) {
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
}