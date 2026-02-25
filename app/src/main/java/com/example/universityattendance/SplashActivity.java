package com.example.universityattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private final long SPLASH_DURATION = 3500;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = FirebaseFirestore.getInstance();
        new Handler().postDelayed(this::checkUserAndRedirect, SPLASH_DURATION);
    }

    private void checkUserAndRedirect() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String role = document.getString("role");
                            Intent intent;
                            if ("admin".equals(role) || "root".equals(role)) {
                                intent = new Intent(SplashActivity.this, AdminMainAct.class);
                                intent.putExtra("role", role);
                            } else if ("teacher".equals(role)) {
                                intent = new Intent(SplashActivity.this, TeacherMainAct.class);
                            } else {
                                intent = new Intent(SplashActivity.this, LoginPage.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginPage.class));
                            finish();
                        }
                    });
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginPage.class));
                finish();
            }, 300);
        }
    }


}
