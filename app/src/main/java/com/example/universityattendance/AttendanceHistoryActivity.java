package com.example.universityattendance;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceHistoryActivity extends AppCompatActivity {

    private static final String TAG = "AttendanceHistoryAct";

    private FirebaseFirestore db;
    private String courseId, sectionId;

    private RecyclerView rvAttendanceTable;
    private AttendanceTableAdapter adapter;
    private List<StudentAttendanceRow> studentRows;
    private List<String> attendanceDates;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        courseId = getIntent().getStringExtra("courseId");
        sectionId = getIntent().getStringExtra("sectionId");

        rvAttendanceTable = findViewById(R.id.rvAttendanceTable);
        rvAttendanceTable.setLayoutManager(new LinearLayoutManager(this));

        attendanceDates = new ArrayList<>();
        studentRows = new ArrayList<>();
        adapter = new AttendanceTableAdapter(this, studentRows, attendanceDates);
        rvAttendanceTable.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadLastThreeAttendances();
    }

    private void loadLastThreeAttendances() {
        db.collection("attendance")
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("sectionId", sectionId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(this::handleAttendanceQuery)
                .addOnFailureListener(e -> Log.e(TAG, "Error getting attendance history", e));
    }

    private void handleAttendanceQuery(QuerySnapshot querySnapshot) {
        if (querySnapshot.isEmpty()) {
            Log.d(TAG, "No attendance history found.");
            return;
        }

        attendanceDates.clear();
        Map<String, Map<String, String>> studentToDateStatusMap = new HashMap<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (QueryDocumentSnapshot attendanceDoc : querySnapshot) {
            Timestamp timestamp = attendanceDoc.getTimestamp("timestamp");
            String formattedDate = (timestamp != null) ? sdf.format(timestamp.toDate()) : "Unknown";

            attendanceDates.add(formattedDate);

            Map<String, Object> attendanceRecords = (Map<String, Object>) attendanceDoc.get("students");
            if (attendanceRecords == null) {
                attendanceRecords = new HashMap<>();
            }

            for (Map.Entry<String, Object> entry : attendanceRecords.entrySet()) {
                String studentEmail = entry.getKey();
                String status = entry.getValue() != null ? entry.getValue().toString() : "-";

                studentToDateStatusMap
                        .computeIfAbsent(studentEmail, k -> new HashMap<>())
                        .put(formattedDate, status);
            }
        }

        // Build studentRows
        studentRows.clear();
        for (Map.Entry<String, Map<String, String>> entry : studentToDateStatusMap.entrySet()) {
            String studentEmail = entry.getKey();
            Map<String, String> statusPerDate = new HashMap<>();

            for (String date : attendanceDates) {
                statusPerDate.put(date, entry.getValue().getOrDefault(date, "-"));
            }

            studentRows.add(new StudentAttendanceRow(studentEmail, statusPerDate));
        }

        populateHeaderRow(attendanceDates);
        adapter.notifyDataSetChanged();
    }

    private void populateHeaderRow(List<String> attendanceDates) {
        LinearLayout headerRow = findViewById(R.id.headerRow);

        if (headerRow.getChildCount() > 1) {
            headerRow.removeViews(1, headerRow.getChildCount() - 1);
        }

        for (String date : attendanceDates) {
            TextView dateHeader = new TextView(this);
            dateHeader.setText(date);
            dateHeader.setTextColor(Color.BLACK);
            dateHeader.setTypeface(null, Typeface.BOLD);
            dateHeader.setPadding(12, 0, 12, 0);
            headerRow.addView(dateHeader);
        }
    }
}
