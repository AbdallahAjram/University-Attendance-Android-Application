### University Attendance System (Firebase & Android)
**Description**: A full-featured Android application built for university faculty and administrators to manage class attendance. Includes Firebase Authentication and Firestore integration for real-time data handling and secure access.

**Technologies**:
- Android Studio (Java)
- Firebase Authentication
- Firebase Firestore (NoSQL)


**Features**:
- Role-based login: Admin, Teacher, Root Admin
- Admin Panel:
  - Add teachers, admins, courses, and sections
  - Auto-generates student data for assigned sections
  - View teacher assignments and attendance logs
- Teacher Panel:
  - Take daily attendance (with Present/Late/Absent options)
  - Automatically refill today's attendance if already taken
  - View last 3 attendance records per section
- Real-time Firestore data sync with proper UTC day tracking
