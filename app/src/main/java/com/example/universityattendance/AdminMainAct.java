package com.example.universityattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMainAct extends AppCompatActivity {

    private Button addTeacherBtn, viewTeachersBtn, viewAttendanceBtn, logoutBtn, addAdminBtn, addCourseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        addTeacherBtn = findViewById(R.id.addTeacherBtn);
        viewTeachersBtn = findViewById(R.id.viewTeachersBtn);
        viewAttendanceBtn = findViewById(R.id.viewAttendanceBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        addAdminBtn = findViewById(R.id.addAdminBtn);
        addCourseBtn = findViewById(R.id.addCourseBtn);

        String role = getIntent().getStringExtra("role");

        if ("root".equals(role)) {
            addAdminBtn.setVisibility(View.VISIBLE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) addCourseBtn.getLayoutParams();
            params.topToBottom = R.id.addAdminBtn;
            addCourseBtn.setLayoutParams(params);

        } else {
            addAdminBtn.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) addCourseBtn.getLayoutParams();
            params.topToBottom = R.id.viewAttendanceBtn;
            addCourseBtn.setLayoutParams(params);
        }

        addTeacherBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminMainAct.this, AddTeacherActivity.class));
        });

        viewTeachersBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminMainAct.this, ViewTeachersActivity.class));
        });

        viewAttendanceBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminMainAct.this, ViewAttendanceActivity.class));
        });

        addAdminBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminMainAct.this, AddAdminActivity.class));
        });

        addCourseBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminMainAct.this, AddCourseActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminMainAct.this, LoginPage.class));
            finish();
        });
    }
}
