package com.example.tluofficehours.view;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tluofficehours.R;
import com.example.tluofficehours.model.TimeSlot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlots;
    private Context context;
    private OnTimeSlotActionListener listener;

    public interface OnTimeSlotActionListener {
        void onTimeSlotDelete(int position);
        void onTimeSlotCheck(int position, boolean isChecked);
        void onTimeChanged(int position, TimeSlot timeSlot);
        void onTimeSlotSelectionChanged();
    }

    public TimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
        // Add some default time slots
        addDefaultTimeSlots();
    }

    private void addDefaultTimeSlots() {
        timeSlots.add(new TimeSlot("9:00", "10:00"));
        timeSlots.add(new TimeSlot("10:00", "11:00"));
        timeSlots.add(new TimeSlot("14:00", "15:00"));
    }

    public void setOnTimeSlotActionListener(OnTimeSlotActionListener listener) {
        this.listener = listener;
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        notifyItemInserted(timeSlots.size() - 1);
    }

    public void removeTimeSlot(int position) {
        if (position >= 0 && position < timeSlots.size()) {
            timeSlots.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, timeSlots.size());
        }
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.bind(timeSlot, position);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartTime, tvEndTime;
        CheckBox cbTimeSlot;
        ImageButton btnDelete;

        TimeSlotViewHolder(View itemView) {
            super(itemView);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            cbTimeSlot = itemView.findViewById(R.id.cb_time_slot);
            btnDelete = itemView.findViewById(R.id.btn_delete_time_slot);
        }

        void bind(TimeSlot timeSlot, int position) {
            tvStartTime.setText(timeSlot.getStartTime());
            tvEndTime.setText(timeSlot.getEndTime());
            cbTimeSlot.setChecked(timeSlot.isSelected());

            // Set click listeners
            tvStartTime.setOnClickListener(v -> showTimePickerDialog(true, position));
            tvEndTime.setOnClickListener(v -> showTimePickerDialog(false, position));

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTimeSlotDelete(position);
                }
                removeTimeSlot(position);
            });

            cbTimeSlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
                timeSlot.setSelected(isChecked);
                if (listener != null) {
                    listener.onTimeSlotCheck(position, isChecked);
                    listener.onTimeSlotSelectionChanged();
                }
            });
        }

        private void showTimePickerDialog(boolean isStartTime, int position) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Parse current time if exists
            String currentTime = isStartTime ?
                    timeSlots.get(position).getStartTime() :
                    timeSlots.get(position).getEndTime();

            if (currentTime != null && currentTime.contains(":")) {
                String[] timeParts = currentTime.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        TimeSlot timeSlot = timeSlots.get(position);

                        if (isStartTime) {
                            timeSlot.setStartTime(time);
                            tvStartTime.setText(time);
                        } else {
                            timeSlot.setEndTime(time);
                            tvEndTime.setText(time);
                        }

                        if (listener != null) {
                            listener.onTimeChanged(position, timeSlot);
                        }
                    }, hour, minute, true);

            timePickerDialog.show();
        }
    }
}
