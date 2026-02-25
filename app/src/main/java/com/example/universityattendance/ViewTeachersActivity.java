package com.example.universityattendance;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewTeachersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TeacherAdapter teacherAdapter;
    private List<Teacher> teacherList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teachers);

        recyclerView = findViewById(R.id.recyclerViewTeachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        teacherAdapter = new TeacherAdapter(teacherList);
        recyclerView.setAdapter(teacherAdapter);

        db = FirebaseFirestore.getInstance();
        loadTeachersFromFirestore();
    }

    private void loadTeachersFromFirestore() {
        db.collection("users")
                .whereEqualTo("role", "teacher")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    teacherList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String courseId = doc.getString("courseId");
                        String sectionId = doc.getString("sectionId");

                        if (name == null || email == null) {
                            Log.w("ViewTeachers", "Skipped teacher with missing name or email: " + doc.getId());
                            continue;
                        }

                        teacherList.add(new Teacher(name, email, courseId, sectionId));
                    }
                    teacherAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                    Log.e("ViewTeachers", "Error loading teachers", e);
                });
    }
}
