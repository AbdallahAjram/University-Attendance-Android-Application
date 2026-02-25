package com.example.universityattendance;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAdminActivity extends AppCompatActivity {

    private EditText fullNameInput, passwordInput;
    private Button addAdminBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        fullNameInput = findViewById(R.id.fullNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        addAdminBtn = findViewById(R.id.addAdminBtn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addAdminBtn.setOnClickListener(v -> {
            String fullName = fullNameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter name and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            addAdminBtn.setEnabled(false);

            generateUniqueAdminEmail(fullName, email -> {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            String uid = authResult.getUser().getUid();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", fullName);
                            userData.put("email", email);
                            userData.put("role", "admin");

                            db.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        String credentials = "email: " + email + "\npassword: " + password;
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Admin Credentials", credentials);
                                        clipboard.setPrimaryClip(clip);

                                        Toast.makeText(this, "Admin added and credentials copied to clipboard", Toast.LENGTH_LONG).show();
                                        fullNameInput.setText("");
                                        passwordInput.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        addAdminBtn.setEnabled(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        addAdminBtn.setEnabled(true);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Auth error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addAdminBtn.setEnabled(true);
                        });
            });
        });
    }

    private void generateUniqueAdminEmail(String fullName, OnEmailGeneratedListener listener) {
        String[] parts = fullName.trim().toLowerCase(Locale.ROOT).split("\\s+");
        String base = parts.length >= 2 ? parts[0] + "." + parts[1] : parts[0];
        String domain = "@uni.edu";
        String emailPrefix = base + ".admin";
        String baseEmail = emailPrefix + domain;

        db.collection("users")
                .whereEqualTo("role", "admin")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = 0;
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getString("email");
                        if (email != null && email.startsWith(emailPrefix)) {
                            count++;
                        }
                    }

                    String uniqueEmail = (count == 0) ? baseEmail : emailPrefix + count + domain;
                    listener.onEmailGenerated(uniqueEmail);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking emails: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    addAdminBtn.setEnabled(true);
                });
    }

    interface OnEmailGeneratedListener {
        void onEmailGenerated(String email);
    }
}
