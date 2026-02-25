package com.example.universityattendance;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendancePage extends AppCompatActivity {

    private RecyclerView rvStudents;
    private Button btnDone;
    private FirebaseFirestore db;
    private String courseId, sectionId;
    private StudentAttendanceAdapter adapter;
    private List<Student> studentList;
    private Map<String, String> latestAttendanceMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_page);

        rvStudents = findViewById(R.id.rvStudents);
        btnDone = findViewById(R.id.btnDone);
        db = FirebaseFirestore.getInstance();

        studentList = new ArrayList<>();
        latestAttendanceMap = new HashMap<>();
        adapter = new StudentAttendanceAdapter(this, studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        courseId = getIntent().getStringExtra("courseId");
        sectionId = getIntent().getStringExtra("sectionId");

        if (courseId == null || sectionId == null) {
            Toast.makeText(this, "Course or section missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadLatestAttendance();

        btnDone.setOnClickListener(v -> saveAttendance());
    }

    private void loadLatestAttendance() {
        db.collection("attendance")
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("sectionId", sectionId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        for (int i = 0; i < snapshot.size(); i++) {
                            Map<String, Object> data = snapshot.getDocuments().get(i).getData();
                            Timestamp timestamp = (Timestamp) data.get("timestamp");

                            if (isSameDay(timestamp.toDate(), new Date())) {
                                Map<String, String> students = (Map<String, String>) data.get("students");
                                if (students != null) {
                                    latestAttendanceMap.putAll(students);
                                }
                                break;
                            }
                        }
                    }
                    loadStudents();
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendancePage", "Error loading previous attendance", e);
                    loadStudents();
                });
    }

    private void loadStudents() {
        db.collection("students")
                .whereEqualTo("registeredCourse", courseId)
                .whereEqualTo("registeredSection", sectionId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    studentList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String uid = doc.getId();

                        if (name != null && email != null) {
                            Student student = new Student(name, email, uid);


                            if (latestAttendanceMap.containsKey(email)) {
                                student.setStatus(latestAttendanceMap.get(email));
                            }

                            studentList.add(student);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendancePage", "Failed to load students", e);
                    Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveAttendance() {
        if (studentList.isEmpty()) {
            Toast.makeText(this, "No students to save attendance for.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("courseId", courseId);
        attendanceData.put("sectionId", sectionId);
        attendanceData.put("timestamp", Timestamp.now());

        Map<String, String> studentsStatus = new HashMap<>();
        for (Student student : studentList) {
            String status = student.getStatus();
            if (status == null || status.equals("none")) {
                status = "absent";
            }
            studentsStatus.put(student.getUid(), status);
        }

        attendanceData.put("students", studentsStatus);

        db.collection("attendance")
                .add(attendanceData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AttendancePage.this, "Attendance saved!", Toast.LENGTH_SHORT).show();
                    btnDone.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendancePage", "Error saving attendance", e);
                    Toast.makeText(AttendancePage.this, "Failed to save attendance.", Toast.LENGTH_SHORT).show();
                });
    }
    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        Calendar c2 = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
