package com.example.universityattendance;

public class Student {
    private String name;
    private String email;
    private String uid;
    private String status;

    public Student() {

    }

    public Student(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.status = "none"; //present/late/absent/ none"default"
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
