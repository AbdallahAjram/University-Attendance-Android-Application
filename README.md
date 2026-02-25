
# University Attendance Android Application

An Android-based university attendance system designed for administrative staff and instructors to manage course sections and student attendance efficiently. Built using Firebase Authentication, Firestore, and Android Studio (Java).

---

## 🔧 Technologies Used

- **Java** – Core language for Android development
- **Firebase Authentication** – Role-based login and session control
- **Firebase Firestore** – Real-time NoSQL cloud database
- **RecyclerView** – Dynamic student and section listings
- **Android Studio** – Development environment

---

## 📱 App Features

### 🔐 Login System
- Firebase-authenticated login for:
  - **Teacher**
  - **Admin**
  - **Root Admin**

### 🧑‍🏫 Teacher Panel
- View assigned **course and section**
- Take daily **attendance**:
  - Mark students as Present, Late, or Absent
  - If attendance is already taken today, values are auto-refilled
- View **last 3 attendance records** for the section

### 🛠️ Admin/Root Panel
- **Add Teacher**:
  - Assign course + section
  - Generates random student list per section
- **Add Admin** (Root-only access)
- **Add Course** with max capacity and section count
- **View Teachers** (list all assignments)
- **View Attendance Logs** (for all sections)
- **Logout**

---

## 🗂️ Folder Structure

```
/app
  /java/com/example/universityattendance
    - LoginActivity.java
    - AdminDashboard.java
    - TeacherDashboard.java
    - AttendancePage.java
    - AddCourseActivity.java
    ...
  /res
    /layout
    /drawable
    ...
google-services.json (⚠️ Do NOT include this in version control)
```

---

## ⚙️ Firebase Setup Instructions

To run this project:

1. Go to [Firebase Console](https://console.firebase.google.com/) and create a new project
2. Enable **Authentication** (Email/Password)
3. Enable **Cloud Firestore** and set rules (if testing):

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

4. Download `google-services.json` and place it inside the `/app` directory
5. Sync the project with Gradle and run the app via Android Studio

---

## 🔐 Important Notes

- `google-services.json` is **excluded** via `.gitignore`
- Use Firebase Emulator Suite for safe local testing (optional)

---

## 📬 Contact

**Email**: [ajrama_04@outlook.com](mailto:ajrama_04@outlook.com)  
**GitHub**: [Abdallah Ajram](https://github.com/)
