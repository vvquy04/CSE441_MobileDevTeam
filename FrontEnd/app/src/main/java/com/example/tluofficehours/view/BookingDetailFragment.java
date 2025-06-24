package com.example.tluofficehours.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.viewmodel.FacultyCalendarViewModel;

public class BookingDetailFragment extends Fragment {
    private FacultyCalendarViewModel viewModel;
    private Booking booking;
    
    private TextView tvStudentName, tvStudentEmail, tvBookingTime, tvStatus, tvReason;
    private Button btnApprove, btnReject, btnCancel, btnComplete, btnBack, btnDetail;

    public static BookingDetailFragment newInstance(Booking booking) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("booking", booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            booking = (Booking) getArguments().getSerializable("booking");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        
        initViews(view);
        setupViewModel();
        displayBookingDetails();
        setupButtonActions();
        
        return view;
    }

    private void initViews(View view) {
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvStudentEmail = view.findViewById(R.id.tvStudentEmail);
        tvBookingTime = view.findViewById(R.id.tvBookingTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvReason = view.findViewById(R.id.tvReason);
        
        btnApprove = view.findViewById(R.id.btnApprove);
        btnReject = view.findViewById(R.id.btnReject);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnComplete = view.findViewById(R.id.btnComplete);
        btnBack = view.findViewById(R.id.btnBack);
        btnDetail = view.findViewById(R.id.btnDetail);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(FacultyCalendarViewModel.class);
        
        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.clearSuccessMessage();
                // Go back to calendar
                requireActivity().onBackPressed();
            }
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    private void displayBookingDetails() {
        if (booking != null) {
            // Get student info from User object
            String studentName = "Không có thông tin";
            String studentEmail = "Không có thông tin";
            String studentInfo = "Không có thông tin";
            
            if (booking.getStudent() != null && booking.getStudent().getStudentProfile() != null) {
                studentName = booking.getStudent().getStudentProfile().getStudentName();
                studentEmail = booking.getStudent().getEmail();
                
                // Tạo thông tin chi tiết sinh viên
                StringBuilder infoBuilder = new StringBuilder(studentEmail);
                String studentCode = booking.getStudent().getStudentProfile().getStudentCode();
                String className = booking.getStudent().getStudentProfile().getClassName();
                
                if (studentCode != null && !studentCode.isEmpty()) {
                    infoBuilder.append(" • Mã SV: ").append(studentCode);
                }
                if (className != null && !className.isEmpty()) {
                    infoBuilder.append(" • Lớp: ").append(className);
                }
                studentInfo = infoBuilder.toString();
            }
            
            tvStudentName.setText(studentName);
            tvStudentEmail.setText(studentInfo);
            tvBookingTime.setText(viewModel.formatTimeForDisplay(booking.getBookingTime()));
            tvStatus.setText(getStatusText(booking.getStatus()));
            tvStatus.setTextColor(viewModel.getStatusColor(booking.getStatus()));
            
            // Use cancellation reason if available
            String reason = booking.getCancellationReason();
            if (reason != null && !reason.isEmpty()) {
                tvReason.setVisibility(View.VISIBLE);
                tvReason.setText("Lý do: " + reason);
            } else {
                tvReason.setVisibility(View.GONE);
            }
            
            updateButtonVisibility();
        }
    }

    private void setupButtonActions() {
        btnBack.setOnClickListener(v -> {
            // Show main content again and go back
            requireActivity().findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            requireActivity().onBackPressed();
        });
        
        btnApprove.setOnClickListener(v -> {
            viewModel.approveBooking(booking.getBookingId());
        });
        
        btnReject.setOnClickListener(v -> {
            // Show dialog to enter reason
            showReasonDialog("Từ chối", (reason) -> {
                viewModel.rejectBooking(booking.getBookingId(), reason);
            });
        });
        
        btnCancel.setOnClickListener(v -> {
            // Show dialog to enter reason
            showReasonDialog("Hủy", (reason) -> {
                viewModel.cancelBooking(booking.getBookingId(), reason);
            });
        });
        
        btnComplete.setOnClickListener(v -> {
            viewModel.markBookingCompleted(booking.getBookingId());
        });
    }

    private void updateButtonVisibility() {
        String status = booking.getStatus();
        
        switch (status) {
            case "pending":
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                break;
            case "confirmed":
                btnApprove.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnComplete.setVisibility(View.VISIBLE);
                break;
            case "completed":
            case "cancelled":
            case "rejected":
                btnApprove.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                break;
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "completed": return "Đã hoàn thành";
            case "cancelled": return "Đã hủy";
            case "rejected": return "Đã từ chối";
            default: return status;
        }
    }

    private void showReasonDialog(String action, ReasonDialogCallback callback) {
        // Simple dialog implementation
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Lý do " + action.toLowerCase());
        
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Nhập lý do...");
        builder.setView(input);
        
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (!reason.isEmpty()) {
                callback.onReasonEntered(reason);
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    interface ReasonDialogCallback {
        void onReasonEntered(String reason);
    }
} 