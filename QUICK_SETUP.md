# Quick Setup Guide - Timetable Feature

## 🚀 Quick Start (5 minutes)

### 1. Start Backend
```bash
cd SEM_full2/sem-tracker
mvn spring-boot:run
```
Backend runs on: http://localhost:8081

### 2. Start Frontend
```bash
cd SEM_full2/frontend_SEM_track
npm run dev
```
Frontend runs on: http://localhost:5173

### 3. Test the Features

#### Basic Usage (No Email)
- Email is **disabled by default** for easy testing
- Schedules will be logged to console instead
- All other features work perfectly!

#### View Today's Schedule
1. Go to http://localhost:5173/timetable
2. Login with your credentials
3. Click "📅 Today" button
4. See your classes for today

#### Add a Regular Schedule
1. Click "➕ Add Schedule"
2. Select "📅 Regular Weekly Schedule"
3. Fill in:
   - Subject: "Data Structures"
   - Day: Monday
   - Start Time: 09:00
   - End Time: 11:00
   - Location: "Room 101"
   - Lecturer: "Dr. Smith"
4. Click "Add"

#### Add a Special Schedule
1. Click "➕ Add Schedule"
2. Select "⭐ Special Schedule (One-time)"
3. Fill in:
   - Subject: "Guest Lecture"
   - Date: Select a future date
   - Start Time: 14:00
   - End Time: 16:00
   - Location: "Auditorium"
   - Description: "AI Trends Seminar"
4. Click "Add"

#### Check Statistics
- Look at the top of the page
- See 4 cards showing:
  - Classes Today
  - Regular Schedules
  - Upcoming Special
  - Total Schedules

## 📧 Enable Email Notifications (Optional)

### For Gmail Users

1. **Get App Password**:
   - Go to: https://myaccount.google.com/security
   - Enable 2-Factor Authentication
   - Go to: App passwords
   - Create password for "Mail"
   - Copy the 16-character password

2. **Update application.properties**:
```properties
# In: SEM_full2/sem-tracker/src/main/resources/application.properties
app.email.enabled=true
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
app.email.from=noreply@semtracker.com
```

3. **Restart Backend**:
```bash
# Stop the running backend (Ctrl+C)
mvn spring-boot:run
```

4. **Verify Email Works**:
   - Check console logs at 7:00 AM
   - Or wait for the next scheduled run
   - Emails sent to users with registered email addresses

### Using Environment Variables (Recommended)
```bash
# Windows PowerShell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password"
$env:EMAIL_ENABLED="true"
mvn spring-boot:run

# Linux/Mac
export EMAIL_USERNAME="your-email@gmail.com"
export EMAIL_PASSWORD="your-app-password"
export EMAIL_ENABLED="true"
mvn spring-boot:run
```

## 🎯 Key Features to Test

### 1. View Modes
- **📅 Today**: Shows today's classes only
- **📆 This Week**: Shows next 7 days
- **⭐ Special Events**: Shows future special schedules
- **📋 All Schedules**: Shows everything
- **📌 Day Selector**: Filter by specific day

### 2. CRUD Operations
- **Create**: Add new schedule
- **Read**: View in different modes
- **Update**: Edit existing schedule
- **Delete**: Remove schedule (with confirmation)

### 3. Statistics Dashboard
- Automatically updates after every operation
- Shows counts and metrics
- Beautiful gradient cards

### 4. Email Notifications
- Daily schedule at 7:00 AM
- Hourly reminders for upcoming classes
- Beautiful HTML formatted emails

## 🔧 Troubleshooting

### Backend Won't Start
```bash
# Check if port 8081 is in use
netstat -ano | findstr :8081

# Kill the process if needed (Windows)
taskkill /PID <process-id> /F

# Or use a different port in application.properties
server.port=8082
```

### Frontend Won't Start
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Database Connection Issues
```bash
# Make sure MySQL container is running
docker ps

# Check connection in application.properties
spring.datasource.url=jdbc:mysql://mysql_container:3306/sem_db
```

### Emails Not Sending
1. Check `app.email.enabled=true`
2. Verify Gmail App Password
3. Check console logs for errors
4. Make sure port 587 is not blocked

## 📊 Testing Schedule

### Morning Test (7:00 AM - 7:05 AM)
- Watch console for email job logs
- Check if emails are sent to users
- Verify email content is correct

### Throughout the Day
- Add schedules at different times
- Edit and delete schedules
- Switch between view modes
- Check statistics update

### Check Different Views
1. **Today View**: See if today's classes show
2. **Week View**: Verify next 7 days
3. **Special View**: Check future events only
4. **Day View**: Filter by Monday, Tuesday, etc.

## 📝 Sample Test Data

### Regular Schedules
```
Monday:
- Data Structures (09:00-11:00, Room 101, Dr. Smith)
- Algorithms (14:00-16:00, Room 102, Prof. Johnson)

Tuesday:
- Database Systems (10:00-12:00, Lab A, Dr. Brown)
- Web Development (15:00-17:00, Lab B, Ms. Davis)

Wednesday:
- Software Engineering (09:00-11:00, Room 201, Dr. Wilson)
- Mobile Dev (13:00-15:00, Lab C, Mr. Taylor)
```

### Special Schedules
```
- Guest Lecture (Next Monday, 14:00-16:00, Auditorium)
- Lab Exam (Next Friday, 10:00-12:00, Lab A)
- Project Presentation (Next Week, 15:00-17:00, Room 301)
```

## 🎨 UI Elements to Notice

### Statistics Cards
- Hover effects (cards lift up)
- Different colors for each metric
- Updates in real-time

### View Buttons
- Active button has gradient background
- Smooth hover transitions
- Clear visual feedback

### Schedule Table
- Special schedules highlighted in yellow
- Hover effect on rows
- Edit/Delete buttons on each row

## 🚨 Important Notes

1. **Email Testing**: Don't test with real user emails during development
2. **Scheduled Jobs**: Run at specific times (7:00 AM for daily email)
3. **User Authentication**: Make sure user is logged in
4. **Database**: Data persists between restarts
5. **Console Logs**: Always check for helpful debug messages

## 📞 Need Help?

Check these resources:
1. Full documentation: `TIMETABLE_ENHANCEMENTS.md`
2. Application logs in console
3. Browser developer console (F12)
4. Network tab for API requests

## ✅ Success Checklist

- [ ] Backend running on port 8081
- [ ] Frontend running on port 5173
- [ ] Can login to application
- [ ] Can see timetable page
- [ ] Statistics dashboard visible
- [ ] Can add regular schedule
- [ ] Can add special schedule
- [ ] Can switch view modes
- [ ] Can edit and delete schedules
- [ ] Email configuration done (optional)

---

**Setup Time**: ~5 minutes
**With Email**: +5 minutes
**Total**: 10 minutes to full functionality!
