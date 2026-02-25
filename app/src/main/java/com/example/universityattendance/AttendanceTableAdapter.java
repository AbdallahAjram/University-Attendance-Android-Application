package com.example.universityattendance;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceTableAdapter extends RecyclerView.Adapter<AttendanceTableAdapter.AttendanceViewHolder> {

    private Context context;
    private List<StudentAttendanceRow> studentList;
    private List<String> dateHeaders; // List of up to 3 dates

    public AttendanceTableAdapter(Context context, List<StudentAttendanceRow> studentList, List<String> dateHeaders) {
        this.context = context;
        this.studentList = studentList;
        this.dateHeaders = dateHeaders;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_table_row, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StudentAttendanceRow student = studentList.get(position);
        String email = student.getStudentEmail();
        String displayEmail = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        holder.tvStudentEmail.setText(displayEmail);

        // Clear previous views if any
        holder.statusContainer.removeAllViews();

        // Add status for each date
        for (String date : dateHeaders) {
            String status = student.getDateToStatusMap().getOrDefault(date, "-");

            TextView statusView = new TextView(context);
            statusView.setText(status);
            statusView.setPadding(12, 0, 12, 0);
            statusView.setTypeface(null, Typeface.NORMAL);
            holder.statusContainer.addView(statusView);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentEmail;
        LinearLayout statusContainer;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            statusContainer = itemView.findViewById(R.id.statusContainer);
        }
    }
}
