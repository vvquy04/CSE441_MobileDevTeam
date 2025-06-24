package com.example.tluofficehours.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tluofficehours.R;
import com.example.tluofficehours.model.FacultyProfile;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private List<FacultyProfile> teachers;
    private Context context;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onTeacherClick(FacultyProfile teacher);
    }

    public TeacherAdapter(Context context, List<FacultyProfile> teachers, OnTeacherClickListener listener) {
        this.context = context;
        this.teachers = teachers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        FacultyProfile teacher = teachers.get(position);
        holder.bind(teacher);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void updateTeachers(List<FacultyProfile> newTeachers) {
        android.util.Log.d("TeacherAdapter", "updateTeachers called with " + (newTeachers != null ? newTeachers.size() : "null") + " teachers");
        this.teachers = newTeachers;
        notifyDataSetChanged();
        android.util.Log.d("TeacherAdapter", "Adapter updated, item count: " + getItemCount());
    }

    class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView teacherTitle;
        TextView teacherName;
        TextView teacherDepartment;
        ImageView teacherImage;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherTitle = itemView.findViewById(R.id.teacherTitle);
            teacherName = itemView.findViewById(R.id.teacherName);
            teacherDepartment = itemView.findViewById(R.id.teacherDepartment);
            teacherImage = itemView.findViewById(R.id.teacherImage);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeacherClick(teachers.get(position));
                }
            });
        }

        public void bind(FacultyProfile teacher) {
            android.util.Log.d("TeacherAdapter", "Binding teacher: " + teacher.getFacultyName());
            android.util.Log.d("TeacherAdapter", "Teacher department ID: " + teacher.getDepartmentId());
            android.util.Log.d("TeacherAdapter", "Teacher department name: " + teacher.getDepartmentName());
            
            teacherTitle.setText("Giảng viên");
            
            String displayName;
            String degree = teacher.getDegree();
            if (degree != null && !degree.isEmpty()) {
                if (!degree.endsWith(".")) degree = degree + ".";
                displayName = degree + " " + teacher.getFacultyName();
            } else {
                displayName = teacher.getFacultyName();
            }
            teacherName.setText(displayName);
            
            // Sử dụng department_name trực tiếp từ model
            String departmentText = "Bộ môn Chưa cập nhật";
            if (teacher.getDepartmentName() != null && !teacher.getDepartmentName().isEmpty()) {
                departmentText = "Bộ môn " + teacher.getDepartmentName();
            }
            android.util.Log.d("TeacherAdapter", "Final department text: " + departmentText);
            teacherDepartment.setText(departmentText);

            // Load teacher image
            if (teacher.getAvatarUrl() != null && !teacher.getAvatarUrl().isEmpty()) {
                android.util.Log.d("TeacherAdapter", "Loading teacher avatar: " + teacher.getAvatarUrl());
                Glide.with(context)
                    .load(teacher.getAvatarUrl())
                    .placeholder(R.drawable.teacher_placeholder_img)
                    .error(R.drawable.teacher_placeholder_img)
                    .into(teacherImage);
            } else {
                android.util.Log.w("TeacherAdapter", "Teacher avatar URL is null or empty");
                teacherImage.setImageResource(R.drawable.teacher_placeholder_img);
            }
        }
    }
} 