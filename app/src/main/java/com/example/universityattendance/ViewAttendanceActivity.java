package com.example.universityattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    private RecyclerView rvSections;
    private SectionListAdapter adapter;
    private List<Teacher> teacherList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        rvSections = findViewById(R.id.rvSections);
        rvSections.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        loadTeacherSections();
    }

    private void loadTeacherSections() {
        db.collection("users")
                .whereEqualTo("role", "teacher")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        teacherList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String courseId = doc.getString("courseId");
                            String sectionId = doc.getString("sectionId");

                            if (name != null && email != null && courseId != null && sectionId != null) {
                                teacherList.add(new Teacher(name, email, courseId, sectionId));
                            }
                        }

                        if (adapter == null) {
                            adapter = new SectionListAdapter(this, teacherList, sectionTeacher -> {
                                Intent intent = new Intent(ViewAttendanceActivity.this, AttendanceHistoryActivity.class);
                                intent.putExtra("courseId", sectionTeacher.courseId);
                                intent.putExtra("sectionId", sectionTeacher.sectionId);
                                startActivity(intent);
                            });
                            rvSections.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load teachers.", Toast.LENGTH_SHORT).show();
                        Log.e("ViewAttendanceActivity", "Error loading teachers", task.getException());
                    }
                });
    }
}
