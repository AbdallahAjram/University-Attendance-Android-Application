package com.example.universityattendance;

public class Teacher {
    public String name;
    public String email;
    public String courseId;
    public String sectionId;

    public Teacher() {}

    public Teacher(String name, String email, String courseId, String sectionId) {
        this.name = name;
        this.email = email;
        this.courseId = courseId;
        this.sectionId = sectionId;
    }
}
