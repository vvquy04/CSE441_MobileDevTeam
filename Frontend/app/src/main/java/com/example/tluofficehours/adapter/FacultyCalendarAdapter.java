package com.example.tluofficehours.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Booking;
import com.example.tluofficehours.viewmodel.FacultyCalendarViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class FacultyCalendarAdapter extends RecyclerView.Adapter<FacultyCalendarAdapter.ViewHolder> {
    private List<Booking> appointments = new ArrayList<>();
    private OnItemClickListener listener;
    private FacultyCalendarViewModel viewModel;

    public FacultyCalendarAdapter() {
        // ViewModel will be set later via setViewModel method
    }

    public void setViewModel(FacultyCalendarViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void updateAppointments(List<Booking> newAppointments) {
        this.appointments = newAppointments != null ? newAppointments : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_faculty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = appointments.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentClass, tvBookingTime, tvStatus, tvPurpose;
        private TextView btnDetail, btnApprove, btnReject, btnCancel, btnComplete;
        private de.hdodenhof.circleimageview.CircleImageView ivStudentAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentClass = itemView.findViewById(R.id.tvStudentClass);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            ivStudentAvatar = itemView.findViewById(R.id.ivStudentAvatar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }

        public void bind(Booking booking) {
            // Set student info
            String studentName = "Không có tên";
            String className = "Không có lớp";
            String avatarUrl = null;
            if (booking.getStudent() != null && booking.getStudent().getStudentProfile() != null) {
                com.example.tluofficehours.model.StudentProfile profile = booking.getStudent().getStudentProfile();
                if (profile.getProfile() != null) {
                    studentName = profile.getProfile().getStudentName() != null ? profile.getProfile().getStudentName() : studentName;
                    className = profile.getProfile().getClassName() != null ? profile.getProfile().getClassName() : className;
                    avatarUrl = profile.getProfile().getAvatar();
                } else {
                    studentName = profile.getStudentName() != null ? profile.getStudentName() : studentName;
                    className = profile.getClassName() != null ? profile.getClassName() : className;
                    avatarUrl = profile.getAvatar();
                }
            }
            tvStudentName.setText(studentName);
            tvStudentClass.setText(className);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .into(ivStudentAvatar);
            } else {
                ivStudentAvatar.setImageResource(R.drawable.avatar_placeholder);
            }

            // Set booking info
            String timeStr = "Không có giờ hẹn";
            String dateStr = "";
            if (booking.getSlot() != null) {
                String start = booking.getSlot().getStartTime();
                String end = booking.getSlot().getEndTime();
                // Parse ngày và giờ
                try {
                    java.text.SimpleDateFormat iso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                    iso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    java.text.SimpleDateFormat outDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    java.text.SimpleDateFormat outTime = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                    java.util.Date startDate = iso.parse(start);
                    java.util.Date endDate = iso.parse(end);
                    if (startDate != null && endDate != null) {
                        dateStr = outDate.format(startDate);
                        timeStr = outTime.format(startDate) + " - " + outTime.format(endDate);
                    }
                } catch (Exception e) {
                    timeStr = formatTimeRange(start, end);
                }
            }
            tvBookingTime.setText(timeStr);
            tvPurpose.setText(booking.getPurpose() != null ? booking.getPurpose() : "Không có mục đích");
            // Trạng thái
            String statusVi = getStatusText(booking.getStatus());
            tvStatus.setText(statusVi);
            int color = android.graphics.Color.GRAY;
            switch (booking.getStatus() != null ? booking.getStatus().toLowerCase() : "") {
                case "pending":
                    color = android.graphics.Color.parseColor("#FFA726"); // cam
                    break;
                case "confirmed":
                    color = android.graphics.Color.parseColor("#388E3C"); // xanh lá
                    break;
                case "completed":
                    color = android.graphics.Color.parseColor("#757575"); // xám
                    break;
                case "cancelled":
                case "rejected":
                    color = android.graphics.Color.parseColor("#D32F2F"); // đỏ
                    break;
            }
            tvStatus.setTextColor(color);
            // Lý do hủy
            TextView tvCancellationReason = itemView.findViewById(R.id.cancellationReason);
            if (tvCancellationReason != null) {
                if ("cancelled".equalsIgnoreCase(booking.getStatus()) && booking.getCancellationReason() != null && !booking.getCancellationReason().isEmpty()) {
                    tvCancellationReason.setVisibility(View.VISIBLE);
                    tvCancellationReason.setText("Lý do hủy: " + booking.getCancellationReason());
                } else {
                    tvCancellationReason.setVisibility(View.GONE);
                }
            }
            // Setup action buttons
            setupClickListeners(booking);
        }

        private String formatTimeRange(String startTime, String endTime) {
            try {
                Date startDate = parseBookingTime(startTime);
                Date endDate = parseBookingTime(endTime);
                
                if (startDate != null && endDate != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    // Force the output to be in UTC, matching the database timezone
                    outputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    return outputFormat.format(startDate) + " - " + outputFormat.format(endDate);
                }
            } catch (Exception e) {
                android.util.Log.e("FacultyCalendarAdapter", "Error formatting time range: " + e.getMessage());
            }
            return "N/A";
        }

        private Date parseBookingTime(String isoTime) throws java.text.ParseException {
            if (isoTime == null || isoTime.isEmpty()) {
                throw new java.text.ParseException("Input date string is null or empty", 0);
            }

            // SimpleDateFormat can't handle 6 digits for microseconds.
            // We need to truncate it to 3 digits (milliseconds).
            if (isoTime.contains(".")) {
                int dotIndex = isoTime.indexOf('.');
                // Check for Z or + (for timezone offset)
                int zIndex = isoTime.indexOf('Z', dotIndex);
                if (zIndex == -1) {
                    zIndex = isoTime.indexOf('+', dotIndex);
                }

                if (zIndex > dotIndex && (zIndex - dotIndex) > 4) {
                     // It has more than 3 fractional digits
                    String timeZonePart = isoTime.substring(zIndex);
                    isoTime = isoTime.substring(0, dotIndex + 4) + timeZonePart;
                }
            }
            
             // Format with milliseconds
             SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
             isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
             try {
                return isoFormat.parse(isoTime);
             } catch (java.text.ParseException e) {
                 // Fallback to format without milliseconds
                 isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                 isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                 return isoFormat.parse(isoTime);
             }
        }

        private void setupClickListeners(Booking booking) {
            btnDetail.setOnClickListener(v -> {
                showSimpleDetailDialog(booking);
            });
            btnApprove.setOnClickListener(v -> {
                // Hiện dialog xác nhận xác nhận lịch hẹn
                android.app.AlertDialog.Builder confirmBuilder = new android.app.AlertDialog.Builder(itemView.getContext());
                View confirmView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_confirm_approve, null);
                confirmBuilder.setView(confirmView);
                final android.app.AlertDialog confirmDialog = confirmBuilder.create();
                confirmDialog.setCanceledOnTouchOutside(false);

                Button btnConfirm = confirmView.findViewById(R.id.btn_dialog_confirm);
                Button btnCancelDialog = confirmView.findViewById(R.id.btn_dialog_cancel);

                btnConfirm.setOnClickListener(v2 -> {
                    if (listener != null) listener.onApproveClick(booking);
                    confirmDialog.dismiss();
                });
                btnCancelDialog.setOnClickListener(v2 -> confirmDialog.dismiss());

                confirmDialog.show();
            });
            btnReject.setOnClickListener(v -> {
                // Hiện dialog xác nhận từ chối
                android.app.AlertDialog.Builder confirmBuilder = new android.app.AlertDialog.Builder(itemView.getContext());
                View confirmView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_confirm_reject, null);
                confirmBuilder.setView(confirmView);
                final android.app.AlertDialog confirmDialog = confirmBuilder.create();
                confirmDialog.setCanceledOnTouchOutside(false);

                Button btnConfirm = confirmView.findViewById(R.id.btn_dialog_confirm);
                Button btnCancelDialog = confirmView.findViewById(R.id.btn_dialog_cancel);

                btnConfirm.setOnClickListener(v2 -> {
                    if (listener != null) listener.onRejectClick(booking);
                    confirmDialog.dismiss();
                });
                btnCancelDialog.setOnClickListener(v2 -> confirmDialog.dismiss());

                confirmDialog.show();
            });
            btnCancel.setOnClickListener(v -> {
                // Hiện dialog xác nhận hủy lịch
                android.app.AlertDialog.Builder confirmBuilder = new android.app.AlertDialog.Builder(itemView.getContext());
                View confirmView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_confirm_cancel, null);
                confirmBuilder.setView(confirmView);
                final android.app.AlertDialog confirmDialog = confirmBuilder.create();
                confirmDialog.setCanceledOnTouchOutside(false);

                Button btnConfirm = confirmView.findViewById(R.id.btn_dialog_confirm);
                Button btnCancelDialog = confirmView.findViewById(R.id.btn_dialog_cancel);

                btnConfirm.setOnClickListener(v2 -> {
                    if (listener != null) listener.onCancelClick(booking);
                    confirmDialog.dismiss();
                });
                btnCancelDialog.setOnClickListener(v2 -> confirmDialog.dismiss());

                confirmDialog.show();
            });
            btnComplete.setOnClickListener(v -> {
                // Hiện dialog xác nhận hoàn thành
                android.app.AlertDialog.Builder confirmBuilder = new android.app.AlertDialog.Builder(itemView.getContext());
                View confirmView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_confirm_complete, null);
                confirmBuilder.setView(confirmView);
                final android.app.AlertDialog confirmDialog = confirmBuilder.create();
                confirmDialog.setCanceledOnTouchOutside(false);

                Button btnConfirm = confirmView.findViewById(R.id.btn_dialog_confirm);
                Button btnCancel = confirmView.findViewById(R.id.btn_dialog_cancel);

                btnConfirm.setOnClickListener(v2 -> {
                    if (listener != null) listener.onCompleteClick(booking);
                    confirmDialog.dismiss();
                });
                btnCancel.setOnClickListener(v2 -> confirmDialog.dismiss());

                confirmDialog.show();
            });
            // Show/hide buttons based on status
            updateButtonVisibility(booking.getStatus());
        }

        private void showSimpleDetailDialog(Booking booking) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_booking_detail_simple, null);
            builder.setView(dialogView);

            // Lấy các view trong dialog
            TextView tvStudentName = dialogView.findViewById(R.id.tvStudentName);
            TextView tvStudentClass = dialogView.findViewById(R.id.tvStudentClass);
            TextView tvBookingTime = dialogView.findViewById(R.id.tvBookingTime);
            TextView tvPurpose = dialogView.findViewById(R.id.tvPurpose);
            TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
            Button btnBack = dialogView.findViewById(R.id.btnBack);
            Button btnApprove = dialogView.findViewById(R.id.btnApprove);

            // Hiển thị thông tin
            String studentName = "Không có thông tin";
            String studentClass = "Không có thông tin";
            
            if (booking.getStudent() != null && booking.getStudent().getStudentProfile() != null) {
                studentName = booking.getStudent().getStudentProfile().getStudentName();
                String className = booking.getStudent().getStudentProfile().getClassName();
                if (className != null && !className.isEmpty()) {
                    studentClass = className;
                }
            }
            
            tvStudentName.setText("Tên sinh viên: " + studentName);
            tvStudentClass.setText("Lớp: " + studentClass);
            
            if (booking.getSlot() != null) {
                String timeRange = formatTimeRange(booking.getSlot().getStartTime(), booking.getSlot().getEndTime());
                tvBookingTime.setText("Thời gian: " + timeRange);
            } else {
                tvBookingTime.setText("Thời gian: Không có thông tin");
            }
            
            tvPurpose.setText("Mục đích: " + (booking.getPurpose() != null ? booking.getPurpose() : "Không có thông tin"));
            tvStatus.setText("Trạng thái: " + getStatusText(booking.getStatus()));

            // Tạo dialog
            final android.app.AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            // Xử lý sự kiện nút
            btnBack.setOnClickListener(v -> dialog.dismiss());
            
            btnApprove.setOnClickListener(v -> {
                if ("pending".equals(booking.getStatus())) {
                    if (listener != null) listener.onApproveClick(booking);
                    dialog.dismiss();
                } else if ("confirmed".equals(booking.getStatus())) {
                    // Hiện dialog xác nhận hoàn thành
                    android.app.AlertDialog.Builder confirmBuilder = new android.app.AlertDialog.Builder(itemView.getContext());
                    View confirmView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_confirm_complete, null);
                    confirmBuilder.setView(confirmView);
                    final android.app.AlertDialog confirmDialog = confirmBuilder.create();
                    confirmDialog.setCanceledOnTouchOutside(false);

                    Button btnConfirm = confirmView.findViewById(R.id.btn_dialog_confirm);
                    Button btnCancel = confirmView.findViewById(R.id.btn_dialog_cancel);

                    btnConfirm.setOnClickListener(v2 -> {
                        if (listener != null) listener.onCompleteClick(booking);
                        confirmDialog.dismiss();
                        dialog.dismiss();
                    });
                    btnCancel.setOnClickListener(v2 -> confirmDialog.dismiss());

                    confirmDialog.show();
                }
            });

            // Hiển thị nút Xác nhận theo trạng thái
            if ("pending".equals(booking.getStatus())) {
                btnApprove.setVisibility(View.VISIBLE);
                btnApprove.setText("Xác nhận");
            } else if ("confirmed".equals(booking.getStatus())) {
                btnApprove.setVisibility(View.VISIBLE);
                btnApprove.setText("Hoàn thành");
            } else {
                btnApprove.setVisibility(View.GONE);
            }

            dialog.show();
        }

        private String getStatusText(String status) {
            if (status == null) return "Không rõ";
            switch (status) {
                case "pending": return "Chờ xác nhận";
                case "confirmed": return "Đã xác nhận";
                case "completed": return "Đã hoàn thành";
                case "cancelled": return "Đã hủy";
                case "rejected": return "Đã từ chối";
                default: return status;
            }
        }

        private void updateButtonVisibility(String status) {
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
    }

    public interface OnItemClickListener {
        void onDetailClick(Booking booking);
        void onApproveClick(Booking booking);
        void onRejectClick(Booking booking);
        void onCancelClick(Booking booking);
        void onCompleteClick(Booking booking);
    }
} 