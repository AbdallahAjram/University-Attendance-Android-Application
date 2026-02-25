package com.example.universityattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.SectionViewHolder> {

    public interface OnSectionClickListener {
        void onSectionClick(Teacher sectionTeacher);
    }

    private Context context;
    private List<Teacher> teacherList;
    private OnSectionClickListener listener;

    public SectionListAdapter(Context context, List<Teacher> teacherList, OnSectionClickListener listener) {
        this.context = context;
        this.teacherList = teacherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_section_row, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.tvCourse.setText("Course: " + teacher.courseId);
        holder.tvSection.setText("Section: " + teacher.sectionId);
        holder.tvTeacher.setText("Teacher: " + teacher.name);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSectionClick(teacher);
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvSection, tvTeacher;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvCourse);
            tvSection = itemView.findViewById(R.id.tvSection);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
        }
    }
}
