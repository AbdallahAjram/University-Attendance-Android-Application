package com.example.universityattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class AddTeacherActivity extends AppCompatActivity {

    private EditText teacherNameEditText, passwordEditText;
    private Spinner courseSpinner, sectionSpinner;
    private Button saveTeacherBtn;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<String> courseList = new ArrayList<>();
    private Map<String, List<String>> courseSectionMap = new HashMap<>();
    private ArrayAdapter<String> sectionAdapter;

    private Set<String> assignedCourseSections = new HashSet<>();

    private final String[] firstNames = {
            // Lebanese / Arab names
            "Ali", "Sara", "Omar", "Layla", "Hassan", "Maya", "Khaled", "Nadine", "Amir", "Dalia",
            "Rami", "Yara", "Samir", "Rana", "Ziad", "Mira", "Fadi", "Lina", "Walid", "Reem",
            "Jad", "Nada", "Bilal", "Jana", "Tarek", "Rola", "Nabil", "Hala", "Youssef", "Mona",
            "Bassam", "Samar", "Karim", "Rita", "Ahmad", "Hiba", "Feras", "Rania", "Sami", "Nour",
            "Mahmoud", "Lamia", "Salim", "Dina", "Kamal", "Joud", "Mazen", "Aya", "Zain", "Rasha",

            // Foreign names
            "John", "Emily", "Michael", "Sophia", "David", "Olivia", "James", "Emma", "Daniel", "Isabella",
            "Matthew", "Mia", "Joseph", "Charlotte", "Andrew", "Amelia", "Christopher", "Harper", "Joshua", "Ella",
            "Benjamin", "Abigail", "Samuel", "Avery", "Ryan", "Lily", "Nathan", "Sofia", "Jonathan", "Scarlett",
            "Thomas", "Grace", "Gabriel", "Chloe", "Alexander", "Victoria", "William", "Madison", "Ethan", "Aria",
            "Jacob", "Zoe", "Lucas", "Penelope", "Mason", "Layla", "Logan", "Riley", "Jackson", "Nora"
    };

    private final String[] lastNames = {
            // Lebanese / Arab last names
            "Haddad", "Nasser", "Jaber", "Farah", "Sayegh", "Khatib", "Saleh", "Rizk", "Khoury", "Maalouf",
            "Saliba", "Sabbagh", "Chamoun", "Assaf", "Tannous", "Hanna", "Mikhael", "Fares", "Karam", "Abi-Rached",
            "Khoury", "Abou Jaoude", "Zahra", "Kassis", "Daouk", "Makhlouf", "Fayad", "Bitar", "Moussawi", "El Khoury",
            "Barakat", "Daher", "Chehab", "Tannir", "Chahine", "Nehme", "Sfeir", "Bazzi", "Salamé", "Khalil",
            "Abou Saab", "Baz", "Melki", "Halabi", "Najjar", "Jomaa", "Matar", "Saad", "Yehia", "Ajram",

            // Foreign last names
            "Smith", "Johnson", "Brown", "Williams", "Jones", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson",
            "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen", "Young", "King", "Wright",
            "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams", "Nelson", "Baker", "Hall",
            "Rivera", "Campbell", "Mitchell", "Carter", "Roberts", "Gonzalez", "Perez", "Sanchez", "Cook", "Morgan"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        teacherNameEditText = findViewById(R.id.teacherNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        courseSpinner = findViewById(R.id.courseSpinner);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        saveTeacherBtn = findViewById(R.id.saveTeacherBtn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchAssignedCourseSections();


        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCourse = courseSpinner.getSelectedItem().toString();
                loadSectionsForCourse(selectedCourse);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        saveTeacherBtn.setOnClickListener(v -> saveTeacher());
    }

    private void loadCourses() {
        db.collection("courses").get().addOnSuccessListener(querySnapshot -> {
            courseList.clear();
            courseSectionMap.clear();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                String courseId = doc.getId();

                db.collection("courses").document(courseId).collection("sections")
                        .get()
                        .addOnSuccessListener(sectionSnapshot -> {
                            List<String> sections = new ArrayList<>();
                            for (QueryDocumentSnapshot sectionDoc : sectionSnapshot) {
                                String sectionId = sectionDoc.getId();
                                String comboKey = courseId + "|" + sectionId;
                                if (!assignedCourseSections.contains(comboKey)) {
                                    sections.add(sectionId);
                                }
                            }

                            if (!sections.isEmpty()) {
                                Collections.sort(sections);
                                courseSectionMap.put(courseId, sections);
                                courseList.add(courseId);

                                ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseList);
                                courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                courseSpinner.setAdapter(courseAdapter);

                                if (courseSpinner.getSelectedItem() != null) {
                                    loadSectionsForCourse(courseSpinner.getSelectedItem().toString());
                                }
                            }
                        });
            }
        });
    }

    private void loadSections(String courseId) {
        db.collection("courses").document(courseId).collection("sections").get().addOnSuccessListener(querySnapshot -> {
            List<String> sections = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                sections.add(doc.getId());
            }
            Collections.sort(sections);
            courseSectionMap.put(courseId, sections);

            if (courseSpinner.getSelectedItem() != null &&
                    courseId.equals(courseSpinner.getSelectedItem().toString())) {
                loadSectionsForCourse(courseId);
            }
        });
    }

    private void loadSectionsForCourse(String courseId) {
        List<String> sections = courseSectionMap.getOrDefault(courseId, new ArrayList<>());
        sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sections);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionAdapter);
    }

    private void saveTeacher() {
        String fullName = teacherNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String courseId = courseSpinner.getSelectedItem().toString();
        String sectionId = sectionSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        generateUniqueEmail(fullName, email -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", fullName);
                        userData.put("email", email);
                        userData.put("courseId", courseId);
                        userData.put("sectionId", sectionId);
                        userData.put("role", "teacher");

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(unused -> {
                                    // Generate students after teacher is created
                                    db.collection("courses").document(courseId).get()
                                            .addOnSuccessListener(courseDoc -> {
                                                if (courseDoc.exists()) {
                                                    int capacity = courseDoc.getLong("capacity").intValue();
                                                    generateRandomStudents(courseId, sectionId, capacity);
                                                }
                                            });

                                    // Update assigned list
                                    assignedCourseSections.add(courseId + "|" + sectionId);

                                    // Remove assigned section from list
                                    List<String> sections = courseSectionMap.get(courseId);
                                    if (sections != null) {
                                        sections.remove(sectionId);
                                        if (sections.isEmpty()) {
                                            courseSectionMap.remove(courseId);
                                            courseList.remove(courseId);
                                        }
                                    }

                                    // Refresh course spinner
                                    ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseList);
                                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    courseSpinner.setAdapter(courseAdapter);

                                    // Refresh section spinner based on new course selection
                                    if (!courseList.isEmpty()) {
                                        courseSpinner.setSelection(0);
                                        loadSectionsForCourse(courseList.get(0));
                                    } else {
                                        sectionSpinner.setAdapter(null); // Clear if no courses
                                        Toast.makeText(this, "All sections assigned.", Toast.LENGTH_SHORT).show();
                                    }

                                    // Copy credentials
                                    String clipboardText = "email: " + email + "\npassword: " + password;
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Teacher Credentials", clipboardText);
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(this, "Teacher added successfully! Credentials copied to clipboard.", Toast.LENGTH_LONG).show();
                                    clearInputs();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Auth error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


    private void generateUniqueEmail(String fullName, EmailCallback callback) {
        String base = fullName.toLowerCase().replaceAll("\\s+", ".");
        String domain = "@uni.edu";
        String email = base + domain;

        checkEmailExists(email, exists -> {
            if (!exists) {
                callback.onEmailReady(email);
            } else {
                int counter = 1;
                tryNextEmail(base, domain, counter, callback);
            }
        });
    }

    private void tryNextEmail(String base, String domain, int counter, EmailCallback callback) {
        String email = base + String.format("%02d", counter) + domain;
        checkEmailExists(email, exists -> {
            if (!exists) {
                callback.onEmailReady(email);
            } else {
                tryNextEmail(base, domain, counter + 1, callback);
            }
        });
    }

    private void checkEmailExists(String email, EmailExistsCallback callback) {
        // Check both 'users' and 'students' collection for existing email to be safer
        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        callback.onCheck(true);
                    } else {
                        db.collection("students").document(email).get()
                                .addOnSuccessListener(doc -> callback.onCheck(doc.exists()));
                    }
                });
    }

    private void generateRandomStudents(String courseId, String sectionId, int capacity) {
        int studentsToCreate = capacity - new Random().nextInt(5); // slightly less than capacity
        Set<String> existingEmails = new HashSet<>();

        for (int i = 0; i < studentsToCreate; i++) {
            String firstName = firstNames[new Random().nextInt(firstNames.length)];
            String lastName = lastNames[new Random().nextInt(lastNames.length)];
            String fullName = firstName + " " + lastName;

            String baseEmail = (firstName + "." + lastName).toLowerCase();
            String email = baseEmail + "@uni.edu";
            int counter = 1;

            while (existingEmails.contains(email)) {
                email = baseEmail + String.format("%02d", counter++) + "@uni.edu";
            }
            existingEmails.add(email);

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("name", fullName);
            studentData.put("email", email);
            studentData.put("registeredCourse", courseId);
            studentData.put("registeredSection", sectionId);
            studentData.put("role", "student");

            db.collection("students").document(email)
                    .set(studentData)
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding student: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchAssignedCourseSections() {
        db.collection("users")
                .whereEqualTo("role", "teacher")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String courseId = doc.getString("courseId");
                        String sectionId = doc.getString("sectionId");
                        if (courseId != null && sectionId != null) {
                            assignedCourseSections.add(courseId + "|" + sectionId);
                        }
                    }
                    loadCourses(); // Now load courses only after we know assigned ones
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching existing teachers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        teacherNameEditText.setText("");
        passwordEditText.setText("");
        courseSpinner.setSelection(0);
        sectionSpinner.setSelection(0);
    }

    interface EmailCallback {
        void onEmailReady(String email);
    }

    interface EmailExistsCallback {
        void onCheck(boolean exists);
    }
}
