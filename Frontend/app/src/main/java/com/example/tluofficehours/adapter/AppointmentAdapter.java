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
import com.example.tluofficehours.api.ApiService;
import com.example.tluofficehours.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.ParseException;
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

    public void setAppointments(List<Appointment> appointments) {
        // Sắp xếp theo ngày giảm dần (mới nhất ở trên)
        Collections.sort(appointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a, Appointment b) {
                return getComparableDate(b.getDate()).compareTo(getComparableDate(a.getDate()));
            }
        });
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = appointments.get(position);
        String displayName;
        String degree = null;
        try {
            degree = appt.getClass().getMethod("getDegree").invoke(appt) != null ? appt.getClass().getMethod("getDegree").invoke(appt).toString() : null;
        } catch (Exception e) {}
        if (degree != null && !degree.isEmpty()) {
            if (!degree.endsWith(".")) degree = degree + ".";
            displayName = degree + " " + appt.getTeacherName();
        } else {
            displayName = appt.getTeacherName();
        }
        holder.teacherName.setText(displayName);
        holder.teacherDepartment.setText(appt.getDepartment());
        holder.appointmentTime.setText(appt.getTime());
        holder.appointmentPurpose.setText(appt.getPurpose());
        holder.cancellationReason.setText("Lý do hủy: " + appt.getCancellationReason());

        // Hiển thị avatar giảng viên
        if (appt.getAvatarUrl() != null && !appt.getAvatarUrl().isEmpty()) {
            Glide.with(context)
                .load(appt.getAvatarUrl())
                .placeholder(R.drawable.teacher_placeholder)
                .error(R.drawable.teacher_placeholder)
                .into(holder.teacherAvatar);
        } else {
            holder.teacherAvatar.setImageResource(R.drawable.teacher_placeholder);
        }

        // Hiển thị header ngày nếu là lịch đầu tiên của ngày
        String currentDate = appt.getDate();
        boolean showHeader = false;
        if (position == 0) {
            showHeader = true;
        } else {
            String prevDate = appointments.get(position - 1).getDate();
            if (!getComparableDate(currentDate).equals(getComparableDate(prevDate))) {
                showHeader = true;
            }
        }
        if (showHeader) {
            holder.dateHeaderText.setVisibility(View.VISIBLE);
            holder.dateHeaderText.setText(formatDateHeader(currentDate));
        } else {
            holder.dateHeaderText.setVisibility(View.GONE);
        }

        // Trạng thái
        switch (appt.getStatus()) {
            case "CONFIRMED":
                holder.appointmentStatus.setText("Đã xác nhận");
                holder.appointmentStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.cancelAppointmentButton.setVisibility(View.VISIBLE);
                holder.rebookAppointmentButton.setVisibility(View.GONE);
                holder.cancellationReason.setVisibility(View.GONE);
                break;
            case "PENDING":
                holder.appointmentStatus.setText("Chờ xác nhận");
                holder.appointmentStatus.setTextColor(Color.parseColor("#FF9800"));
                holder.cancelAppointmentButton.setVisibility(View.VISIBLE);
                holder.rebookAppointmentButton.setVisibility(View.GONE);
                holder.cancellationReason.setVisibility(View.GONE);
                break;
            case "CANCELLED":
                holder.appointmentStatus.setText("Đã hủy");
                holder.appointmentStatus.setTextColor(Color.parseColor("#F44336"));
                holder.cancelAppointmentButton.setVisibility(View.GONE);
                holder.rebookAppointmentButton.setVisibility(View.VISIBLE);
                holder.cancellationReason.setVisibility(View.VISIBLE);
                break;
            case "COMPLETED":
                holder.appointmentStatus.setText("Đã hoàn thành");
                holder.appointmentStatus.setTextColor(Color.BLACK);
                holder.cancelAppointmentButton.setVisibility(View.GONE);
                holder.rebookAppointmentButton.setVisibility(View.GONE);
                holder.cancellationReason.setVisibility(View.GONE);
                break;
            default:
                holder.appointmentStatus.setText(appt.getStatus());
                holder.cancelAppointmentButton.setVisibility(View.GONE);
                holder.rebookAppointmentButton.setVisibility(View.GONE);
                holder.cancellationReason.setVisibility(View.GONE);
        }

        holder.cancelAppointmentButton.setOnClickListener(v -> {
            showCancelDialog(v.getContext(), reason -> {
                if (actionListener != null) actionListener.onCancel(appt, reason);
            });
        });
        holder.rebookAppointmentButton.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onRebook(appt);
        });
        holder.viewDetailsButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, com.example.tluofficehours.AppointmentDetailActivity.class);
            intent.putExtra("teacherName", displayName);
            intent.putExtra("department", appt.getDepartment());
            intent.putExtra("avatarUrl", appt.getAvatarUrl());
            intent.putExtra("date", appt.getDate());
            intent.putExtra("time", appt.getTime());
            intent.putExtra("purpose", appt.getPurpose());
            intent.putExtra("room", appt.getRoom());
            intent.putExtra("status", appt.getStatus());
            intent.putExtra("cancellationReason", appt.getCancellationReason());
            intent.putExtra("appointmentId", appt.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeaderText, teacherName, teacherDepartment, appointmentTime, appointmentPurpose, appointmentStatus;
        TextView cancelAppointmentButton, rebookAppointmentButton, viewDetailsButton, cancellationReason;
        CircleImageView teacherAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeaderText = itemView.findViewById(R.id.dateHeaderText);
            teacherName = itemView.findViewById(R.id.teacherName);
            teacherDepartment = itemView.findViewById(R.id.teacherDepartment);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentPurpose = itemView.findViewById(R.id.appointmentPurpose);
            appointmentStatus = itemView.findViewById(R.id.appointmentStatus);
            cancelAppointmentButton = itemView.findViewById(R.id.cancelAppointmentButton);
            rebookAppointmentButton = itemView.findViewById(R.id.rebookAppointmentButton);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            cancellationReason = itemView.findViewById(R.id.cancellationReason);
            teacherAvatar = itemView.findViewById(R.id.teacherAvatar);
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

    @SuppressLint("ResourceType")
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
        // Tuỳ chỉnh màu nút nếu muốn
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.blue_900));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray));
    }
} 