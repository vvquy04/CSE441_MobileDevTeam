package com.example.tluofficehours.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.Notification;
import com.example.tluofficehours.viewmodel.NotificationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {
    private NotificationViewModel viewModel;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter();
        notificationRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.getNotifications().observe(this, notifications -> {
            if (notifications != null) {
                adapter.setNotifications(notifications);
            } else {
                Toast.makeText(this, "Không thể tải thông báo", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.loadNotifications();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    // Adapter nội bộ cho RecyclerView
    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
        private List<Notification> notifications;
        public void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
            notifyDataSetChanged();
        }
        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }
        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.bind(notification);
        }
        @Override
        public int getItemCount() {
            return notifications == null ? 0 : notifications.size();
        }
    }

    // ViewHolder nội bộ cho item notification
    private static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private android.widget.TextView tvDate, tvTime, tvTitle, tvMessage;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
        public void bind(Notification notification) {
            // Format ngày/giờ
            String createdAt = notification.getCreatedAt();
            String date = "", time = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date d = sdf.parse(createdAt);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                date = dateFormat.format(d);
                time = timeFormat.format(d);
            } catch (Exception e) {
                date = createdAt;
                time = "";
            }
            tvDate.setText(date);
            tvTime.setText(time);
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
        }
    }
}