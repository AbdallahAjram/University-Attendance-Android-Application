package com.example.universityattendance;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {

    private Context context;
    private List<Student> students;

    public StudentAttendanceAdapter(Context context, List<Student> students) {
        this.context = context;
        this.students = students;
    }


    @NonNull
    @Override
    public StudentAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAttendanceAdapter.ViewHolder holder, int position) {
        Student student = students.get(position);

        holder.nameTextView.setText(student.getName());
        holder.emailTextView.setText(student.getEmail());

        resetButtonColors(holder);

        switch (student.getStatus()) {
            case "present":
                holder.presentBtn.setBackgroundColor(Color.parseColor("#A5D6A7"));
                break;
            case "late":
                holder.lateBtn.setBackgroundColor(Color.parseColor("#FFF59D"));
                break;
            case "absent":
                holder.absentBtn.setBackgroundColor(Color.parseColor("#EF9A9A"));
                break;
            default:
                // no color for none
                break;
        }

        holder.presentBtn.setOnClickListener(v -> {
            student.setStatus("present");
            notifyItemChanged(position);
        });

        holder.lateBtn.setOnClickListener(v -> {
            student.setStatus("late");
            notifyItemChanged(position);
        });

        holder.absentBtn.setOnClickListener(v -> {
            student.setStatus("absent");
            notifyItemChanged(position);
        });
    }

    private void resetButtonColors(ViewHolder holder) {
        holder.presentBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.lateBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.absentBtn.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, emailTextView;
        Button presentBtn, lateBtn, absentBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.studentName);
            emailTextView = itemView.findViewById(R.id.studentEmail);
            presentBtn = itemView.findViewById(R.id.btnPresent);
            lateBtn = itemView.findViewById(R.id.btnLate);
            absentBtn = itemView.findViewById(R.id.btnAbsent);
        }
    }
}
