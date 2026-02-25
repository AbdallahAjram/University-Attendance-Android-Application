package com.example.universityattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.core.content.ContextCompat;

public class TeacherMainAct extends AppCompatActivity {

    private TextView textCourseId, textSectionId, textCapacity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String courseId, sectionId;

    Button btnLogout, btnViewLastAttendances;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        textCourseId = findViewById(R.id.textCourseId);
        textSectionId = findViewById(R.id.textSectionId);
        textCapacity = findViewById(R.id.textCapacity);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewLastAttendances= findViewById(R.id.btnViewLastAttendances);

        // Assuming this is the container of course info (e.g. CardView or LinearLayout)
        // Make sure to add this ID to your XML layout
        View courseCardView = findViewById(R.id.courseCard);

        textCourseId.setText("Course: Loading...");
        textSectionId.setText("Section: ...");
        textCapacity.setText("Capacity: ...");

        int placeholderColor = ContextCompat.getColor(this, R.color.gray);
        textCourseId.setTextColor(placeholderColor);
        textSectionId.setTextColor(placeholderColor);
        textCapacity.setTextColor(placeholderColor);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        courseCardView.setOnClickListener(v -> {
            if (courseId != null && sectionId != null) {
                Intent intent = new Intent(TeacherMainAct.this, AttendancePage.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("sectionId", sectionId);
                startActivity(intent);
            }
        });
        btnViewLastAttendances.setOnClickListener(v -> {
            if (courseId != null && sectionId != null) {
                Intent intent = new Intent(TeacherMainAct.this, AttendanceHistoryActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("sectionId", sectionId);
                startActivity(intent);
            }
        });
        loadTeacherData();

        int normalColor = ContextCompat.getColor(this, R.color.black);
        textCourseId.setTextColor(normalColor);
        textSectionId.setTextColor(normalColor);
        textCapacity.setTextColor(normalColor);
    }

    private void loadTeacherData() {
        String currentEmail = mAuth.getCurrentUser().getEmail();

        db.collection("users")
                .whereEqualTo("email", currentEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot doc = query.getDocuments().get(0);
                        courseId = doc.getString("courseId");
                        sectionId = doc.getString("sectionId");

                        textCourseId.setText("Course: " + courseId);
                        if (sectionId != null && sectionId.startsWith("section")) {
                            String sectionNumber = sectionId.substring("section".length());
                            textSectionId.setText("Section: " + sectionNumber);
                        } else {
                            textSectionId.setText("Section: " + sectionId);
                        }
                        fetchClassCapacity();
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("TeacherMainAct", "Error loading teacher info", e));
    }

    private void fetchClassCapacity() {
        db.collection("students")
                .whereEqualTo("registeredCourse", courseId)
                .whereEqualTo("registeredSection", sectionId)
                .get()
                .addOnSuccessListener(query -> {
                    int count = query.size();
                    textCapacity.setText("Capacity: " + count + " students");
                })
                .addOnFailureListener(e ->
                        Log.e("TeacherMainAct", "Error loading student count", e));
    }
}
