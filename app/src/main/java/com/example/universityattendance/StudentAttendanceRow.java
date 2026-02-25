package com.example.universityattendance;

import java.util.Map;

public class StudentAttendanceRow {
    private String studentEmail;
    private Map<String, String> dateToStatusMap;

    public StudentAttendanceRow(String studentEmail, Map<String, String> dateToStatusMap) {
        this.studentEmail = studentEmail;
        this.dateToStatusMap = dateToStatusMap;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Map<String, String> getDateToStatusMap() {
        return dateToStatusMap;
    }
}
