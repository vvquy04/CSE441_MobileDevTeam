package com.example.tluofficehours.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Appointment;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointments;
    private Context context;
    private OnAppointmentActionListener actionListener;
    private String displayMode = "upcoming"; // "upcoming" hoặc "history"

    public interface OnAppointmentActionListener {
        void onCancel(Appointment appointment, String reason);
        void onRebook(Appointment appointment);
        void onViewDetails(Appointment appointment);
    }

    public interface OnCancelConfirmedListener {
        void onCancelConfirmed(String reason);
    }

    public AppointmentAdapter(Context context, List<Appointment> appointments, OnAppointmentActionListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.actionListener = listener;
    }

    public void setDisplayMode(String mode) {
        this.displayMode = mode;
        filterAppointments();
    }

    private void filterAppointments() {
        if (appointments == null) return;
        List<Appointment> filtered = new java.util.ArrayList<>();
        for (Appointment appt : appointments) {
            String status = appt.getStatus();
            if ("upcoming".equals(displayMode)) {
                if ("pending".equalsIgnoreCase(status) || "confirmed".equalsIgnoreCase(status)) {
                    filtered.add(appt);
                }
            } else if ("history".equals(displayMode)) {
                if ("completed".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
                    filtered.add(appt);
                }
            }
        }
        this.appointments = filtered;
        notifyDataSetChanged();
    }

    public void setAppointments(List<Appointment> appointments) {
        // Sắp xếp theo ngày giảm dần (mới nhất ở trên)
        Collections.sort(appointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a, Appointment b) {
                return getComparableDate(b.getDate()).compareTo(getComparableDate(a.getDate()));
            }
        });
        this.appointments = appointments;
        filterAppointments();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_student, parent, false);
        return new ViewHolder(view, actionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = appointments.get(position);
        holder.bind(appt, position);
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentClass, appointmentTime, appointmentPurpose, appointmentStatus;
        TextView cancelAppointmentButton, rebookAppointmentButton, viewDetailsButton, cancellationReason;
        de.hdodenhof.circleimageview.CircleImageView studentAvatar;
        private final OnAppointmentActionListener actionListener;
        public ViewHolder(@NonNull View itemView, OnAppointmentActionListener actionListener) {
            super(itemView);
            this.actionListener = actionListener;
            studentName = itemView.findViewById(R.id.teacherName);
            studentClass = itemView.findViewById(R.id.teacherDepartment);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentPurpose = itemView.findViewById(R.id.appointmentPurpose);
            appointmentStatus = itemView.findViewById(R.id.appointmentStatus);
            cancelAppointmentButton = itemView.findViewById(R.id.cancelAppointmentButton);
            rebookAppointmentButton = itemView.findViewById(R.id.rebookAppointmentButton);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            cancellationReason = itemView.findViewById(R.id.cancellationReason);
            studentAvatar = itemView.findViewById(R.id.teacherAvatar);
        }
        public void bind(Appointment appt, int position) {
            studentName.setText(appt.getTeacherName());
            studentClass.setText(appt.getDepartment());
            appointmentTime.setText(appt.getTime());
            appointmentPurpose.setText(appt.getPurpose());
            String statusVi = getStatusTextVi(appt.getStatus());
            appointmentStatus.setText(statusVi);
            // Đổi màu trạng thái
            int color = android.graphics.Color.GRAY;
            switch (appt.getStatus() != null ? appt.getStatus().toLowerCase() : "") {
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
            appointmentStatus.setTextColor(color);
            cancellationReason.setText("Lý do hủy: " + appt.getCancellationReason());
            // Hiển thị avatar sinh viên
            if (appt.getAvatarUrl() != null && !appt.getAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(appt.getAvatarUrl())
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(studentAvatar);
            } else {
                studentAvatar.setImageResource(R.drawable.avatar_placeholder);
            }
            // Ẩn nút hủy nếu không phải pending/confirmed
            if (cancelAppointmentButton != null) {
                String st = appt.getStatus() != null ? appt.getStatus().toLowerCase() : "";
                if ("pending".equals(st) || "confirmed".equals(st)) {
                    cancelAppointmentButton.setVisibility(View.VISIBLE);
                    cancelAppointmentButton.setOnClickListener(v -> {
                        showCancelDialog(itemView.getContext(), reason -> {
                            if (actionListener != null) actionListener.onCancel(appt, reason);
                        });
                    });
                } else {
                    cancelAppointmentButton.setVisibility(View.GONE);
                    cancelAppointmentButton.setOnClickListener(null);
                }
            }
            if (viewDetailsButton != null) {
                viewDetailsButton.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onViewDetails(appt);
                });
            }
        }
        private String getStatusTextVi(String status) {
            if (status == null) return "Không rõ";
            switch (status.toLowerCase()) {
                case "pending": return "Chờ xác nhận";
                case "confirmed": return "Đã xác nhận";
                case "completed": return "Đã hoàn thành";
                case "cancelled": return "Đã hủy";
                case "rejected": return "Đã từ chối";
                default: return status;
            }
        }
        private void showCancelDialog(Context context, OnCancelConfirmedListener listener) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Xác nhận hủy lịch hẹn của bạn?");
            final android.widget.EditText reasonEdit = new android.widget.EditText(context);
            reasonEdit.setHint("Lý do hủy");
            reasonEdit.setPadding(32, 32, 32, 32);
            builder.setView(reasonEdit);
            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                String reason = reasonEdit.getText().toString().trim();
                if (reason.isEmpty()) {
                    android.widget.Toast.makeText(context, "Vui lòng nhập lý do hủy!", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    listener.onCancelConfirmed(reason);
                }
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.blue_900));
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    private String formatDateHeader(String dateStr) {
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy"};
        for (String fmt : formats) {
            try {
                SimpleDateFormat input = new SimpleDateFormat(fmt);
                Date date = input.parse(dateStr);
                // Đảm bảo tiếng Việt
                SimpleDateFormat output = new SimpleDateFormat("EEEE, dd/MM/yyyy", new java.util.Locale("vi", "VN"));
                String dayOfWeek = output.format(date);
                // Viết hoa chữ cái đầu
                return dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1);
            } catch (Exception ignored) {}
        }
        return dateStr;
    }

    private String getComparableDate(String dateStr) {
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy"};
        for (String fmt : formats) {
            try {
                SimpleDateFormat input = new SimpleDateFormat(fmt);
                Date date = input.parse(dateStr);
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                return output.format(date);
            } catch (Exception ignored) {}
        }
        return dateStr;
    }

    private String normalizeDegree(String s) {
        if (s == null) return "";
        s = s.toLowerCase().replace(".", "").replace(" ", "");
        if (s.startsWith("thacs") || s.startsWith("ths")) return "ths";
        if (s.startsWith("tiensi") || s.startsWith("ts")) return "ts";
        // Thêm các học vị khác nếu cần
        return s;
    }

    private boolean hasDegreePrefix(String name) {
        if (name == null) return false;
        String n = name.trim().toLowerCase();
        return n.startsWith("ths.") || n.startsWith("th.s.") || n.startsWith("thạc sĩ") ||
               n.startsWith("ts.") || n.startsWith("tiến sĩ");
    }
} 