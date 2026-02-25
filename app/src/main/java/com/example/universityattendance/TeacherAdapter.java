package com.example.universityattendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private final List<Teacher> teacherList;

    public TeacherAdapter(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_card, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.nameText.setText("👤 " + teacher.name);
        holder.emailText.setText("📧 " + teacher.email);

        String courseSection = "📘 ";
        if (teacher.courseId != null && teacher.sectionId != null) {
            courseSection += teacher.courseId + " (" + teacher.sectionId + ")";
        } else {
            courseSection += "No course assigned";
        }
        holder.coursesText.setText(courseSection);
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, coursesText;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.teacherName);
            emailText = itemView.findViewById(R.id.teacherEmail);
            coursesText = itemView.findViewById(R.id.teacherCourses);
        }
    }
}
