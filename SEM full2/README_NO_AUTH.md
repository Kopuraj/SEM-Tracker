# SEM Tracker - No Authentication Version

## ✅ **AUTHENTICATION REMOVED - APP NOW WORKS WITHOUT LOGIN**

### **What Changed:**
- ❌ Removed all authentication/login requirements
- ❌ Removed JWT tokens and security filters
- ❌ Removed login/signup pages
- ✅ All endpoints are now public and accessible
- ✅ App starts directly on home page
- ✅ All functions work without any login

### **How to Run:**

1. **Start Backend:**
   ```bash
   cd sem-tracker
   mvn spring-boot:run
   ```

2. **Start Frontend:**
   ```bash
   cd frontend_SEM_track
   npm run dev
   ```

3. **Access App:**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080

### **Features Working:**

✅ **Timetable Management:**
- Add, view, edit, delete schedules
- Regular and special schedules
- Day-based filtering

✅ **Attendance Tracking:**
- Add attendance records
- View attendance history
- Track hours and modules

✅ **Quiz/Exam Management:**
- Add quiz/exam records
- View grades and percentages
- Pass/fail status tracking

### **Database:**
- Uses H2 in-memory database (no setup needed)
- Data resets on each restart
- Access H2 console at: http://localhost:8080/h2-console
- Username: `sa`, Password: `password`

### **Default Data:**
- All records use `default_user` as student ID
- 5 default subjects created automatically (Math, Physics, Chemistry, English, Computer Science)

### **Navigation:**
- Home page: Overview
- Timetable: Schedule management
- Attendance: Attendance tracking
- Quiz/Exams: Grade management
- About Us: Information
- Contact: Contact details

**The app is now fully functional without any authentication barriers!**
