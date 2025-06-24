package com.example.tluofficehours.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.viewmodel.FacultyCalendarViewModel;

public class BookingDetailFragment extends Fragment {
    private FacultyCalendarViewModel viewModel;
    private Booking booking;
    
    private TextView tvStudentName, tvStudentEmail, tvBookingTime, tvStatus, tvReason, tvBookingDate;
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
        Log.d("BookingDetail", "initViews called");
        
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvStudentEmail = view.findViewById(R.id.tvStudentEmail);
        tvBookingTime = view.findViewById(R.id.tvBookingTime);
        tvBookingDate = view.findViewById(R.id.tvBookingDate);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvReason = view.findViewById(R.id.tvReason);
        
        btnApprove = view.findViewById(R.id.btnApprove);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnComplete = view.findViewById(R.id.btnComplete);
        btnBack = view.findViewById(R.id.btnBack);
        
        // Chú ý: btnReject và btnDetail có thể không tồn tại trong layout
        btnReject = view.findViewById(R.id.btnReject);
        btnDetail = view.findViewById(R.id.btnDetail);
        
        Log.d("BookingDetail", "btnApprove: " + (btnApprove != null));
        Log.d("BookingDetail", "btnCancel: " + (btnCancel != null));
        Log.d("BookingDetail", "btnComplete: " + (btnComplete != null));
        Log.d("BookingDetail", "btnBack: " + (btnBack != null));
        Log.d("BookingDetail", "btnReject: " + (btnReject != null));
        Log.d("BookingDetail", "btnDetail: " + (btnDetail != null));
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
            tvBookingDate.setText(booking.getBookingDate());
            tvBookingTime.setText(booking.getBookingTimeRange());
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
        Log.d("BookingDetail", "setupButtonActions called");
        
        btnBack.setOnClickListener(v -> {
            Log.d("BookingDetail", "Back button clicked");
            // Show main content again and go back
            requireActivity().findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            requireActivity().onBackPressed();
        });
        
        btnApprove.setOnClickListener(v -> {
            Log.d("BookingDetail", "Approve button clicked");
            if (booking != null) {
                showApproveDialog();
            } else {
                Log.e("BookingDetail", "Booking is null");
            }
        });
        
        // Kiểm tra nếu btnReject tồn tại (có thể không có trong layout)
        if (btnReject != null) {
            btnReject.setOnClickListener(v -> {
                Log.d("BookingDetail", "Reject button clicked");
                if (booking != null) {
                    showRejectDialog();
                } else {
                    Log.e("BookingDetail", "Booking is null");
                }
            });
        } else {
            Log.d("BookingDetail", "btnReject is null");
        }
        
        btnCancel.setOnClickListener(v -> {
            Log.d("BookingDetail", "Cancel button clicked");
            if (booking != null) {
                showCancelDialog();
            } else {
                Log.e("BookingDetail", "Booking is null");
            }
        });
        
        btnComplete.setOnClickListener(v -> {
            Log.d("BookingDetail", "Complete button clicked");
            if (booking != null) {
                showCompleteDialog();
            } else {
                Log.e("BookingDetail", "Booking is null");
            }
        });
        
        Log.d("BookingDetail", "All button listeners set");
    }

    private void showReasonDialog(String title, OnReasonEnteredListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setHint("Nhập lý do...");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String reason = input.getText().toString();
            listener.onReasonEntered(reason);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showApproveDialog() {
        Log.d("BookingDetail", "showApproveDialog called");
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_approve, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Đảm bảo dialog background trong suốt
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        AppCompatButton btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        Log.d("BookingDetail", "btnCancel: " + (btnCancel != null));
        Log.d("BookingDetail", "btnConfirm: " + (btnConfirm != null));

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Log.d("BookingDetail", "Cancel button clicked");
                Toast.makeText(requireContext(), "Đã hủy", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
        
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                Log.d("BookingDetail", "Confirm button clicked");
                Toast.makeText(requireContext(), "Đang xác nhận...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                viewModel.approveBooking(Integer.parseInt(booking.getBookingId()));
            });
        }

        dialog.show();
        Log.d("BookingDetail", "Dialog shown");
    }

    private void showRejectDialog() {
        Log.d("BookingDetail", "showRejectDialog called");
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_reject, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        AppCompatButton btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Log.d("BookingDetail", "Reject Cancel button clicked");
                Toast.makeText(requireContext(), "Đã hủy", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
        
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                Log.d("BookingDetail", "Reject Confirm button clicked");
                Toast.makeText(requireContext(), "Đang từ chối...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showReasonDialog("Từ chối lịch hẹn", (reason) -> {
                    viewModel.rejectBooking(Integer.parseInt(booking.getBookingId()), reason);
                });
            });
        }

        dialog.show();
    }

    private void showCancelDialog() {
        Log.d("BookingDetail", "showCancelDialog called");
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_cancel, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        AppCompatButton btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Log.d("BookingDetail", "Cancel Cancel button clicked");
                Toast.makeText(requireContext(), "Đã hủy", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
        
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                Log.d("BookingDetail", "Cancel Confirm button clicked");
                Toast.makeText(requireContext(), "Đang hủy lịch...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showReasonDialog("Hủy lịch hẹn", (reason) -> {
                    viewModel.cancelBooking(Integer.parseInt(booking.getBookingId()), reason);
                });
            });
        }

        dialog.show();
    }

    private void showCompleteDialog() {
        Log.d("BookingDetail", "showCompleteDialog called");
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_complete, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        AppCompatButton btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Log.d("BookingDetail", "Complete Cancel button clicked");
                Toast.makeText(requireContext(), "Đã hủy", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
        
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                Log.d("BookingDetail", "Complete Confirm button clicked");
                Toast.makeText(requireContext(), "Đang hoàn thành...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                viewModel.markBookingCompleted(Integer.parseInt(booking.getBookingId()));
            });
        }

        dialog.show();
    }

    interface OnReasonEnteredListener {
        void onReasonEntered(String reason);
    }

    private void updateButtonVisibility() {
        if (booking == null) return;
        
        String status = booking.getStatus();
        
        switch (status) {
            case "pending":
                btnApprove.setVisibility(View.VISIBLE);
                if (btnReject != null) {
                    btnReject.setVisibility(View.VISIBLE);
                }
                btnCancel.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                break;
            case "confirmed":
                btnApprove.setVisibility(View.GONE);
                if (btnReject != null) {
                    btnReject.setVisibility(View.GONE);
                }
                btnCancel.setVisibility(View.VISIBLE);
                btnComplete.setVisibility(View.VISIBLE);
                break;
            case "completed":
            case "cancelled":
            case "rejected":
                btnApprove.setVisibility(View.GONE);
                if (btnReject != null) {
                    btnReject.setVisibility(View.GONE);
                }
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
}