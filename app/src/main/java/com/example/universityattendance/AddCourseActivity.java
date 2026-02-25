package com.example.universityattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCourseActivity extends AppCompatActivity {

    private EditText courseNameEditText, courseNumberEditText, courseCapacityEditText, courseSectionsEditText;
    private Button saveCourseBtn;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        courseNameEditText = findViewById(R.id.courseNameEditText);
        courseNumberEditText = findViewById(R.id.courseNumberEditText);
        courseCapacityEditText = findViewById(R.id.courseCapacityEditText);
        courseSectionsEditText = findViewById(R.id.courseSectionsEditText);
        saveCourseBtn = findViewById(R.id.saveCourseBtn);

        db = FirebaseFirestore.getInstance();

        saveCourseBtn.setOnClickListener(v -> {
            String name = courseNameEditText.getText().toString().trim();
            String number = courseNumberEditText.getText().toString().trim();
            String capacityStr = courseCapacityEditText.getText().toString().trim();
            String sectionsStr = courseSectionsEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(sectionsStr)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity;
            int sectionsCount;
            try {
                capacity = Integer.parseInt(capacityStr);
                sectionsCount = Integer.parseInt(sectionsStr);
                if (sectionsCount < 1) {
                    Toast.makeText(this, "Sections must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Capacity and Sections must be valid numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCourseToFirestore(name, number, capacity, sectionsCount);
        });
    }

    private void saveCourseToFirestore(String name, String number, int capacity, int sectionsCount) {
        String courseId = name.toUpperCase() + "_" + number;

        Map<String, Object> course = new HashMap<>();
        course.put("name", name);
        course.put("number", number);
        course.put("capacity", capacity);
        course.put("sectionsCount", sectionsCount);

        db.collection("courses")
                .document(courseId)
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    createSections(courseId, sectionsCount);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCourseActivity.this, "Error saving course: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createSections(String courseId, int sectionsCount) {
        for (int i = 1; i <= sectionsCount; i++) {
            Map<String, Object> section = new HashMap<>();
            section.put("name", "Section " + i);

            db.collection("courses")
                    .document(courseId)
                    .collection("sections")
                    .document("section" + i)
                    .set(section)
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddCourseActivity.this, "Error creating section: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }

        Toast.makeText(AddCourseActivity.this, "Course and sections saved successfully!", Toast.LENGTH_SHORT).show();
        clearInputs();
    }

    private void clearInputs() {
        courseNameEditText.setText("");
        courseNumberEditText.setText("");
        courseCapacityEditText.setText("");
        courseSectionsEditText.setText("");
    }
}