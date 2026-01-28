# Timetable Feature Enhancements

## Overview
This document describes the comprehensive enhancements made to the SEM Tracker timetable feature to make it more efficient and user-friendly for managing semester activities.

## ✨ New Features

### 1. **Daily Email Notifications** 📧
- **Automated Daily Schedule Emails**: Users receive beautiful HTML-formatted emails every morning at 7:00 AM with their day's schedule
- **Email Content Includes**:
  - All regular classes scheduled for that day of the week
  - Special events scheduled for that specific date
  - Time, location, lecturer, and description for each class
  - Visual distinction between regular and special schedules
  - Friendly message if no classes are scheduled

- **Email Configuration** (`application.properties`):
```properties
# Email Settings
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${EMAIL_PASSWORD:your-app-password}
app.email.enabled=${EMAIL_ENABLED:false}

# Schedule Settings
app.schedule.daily-email.time=07:00
app.schedule.daily-email.enabled=${DAILY_EMAIL_ENABLED:true}
```

### 2. **Enhanced Backend APIs** 🔌

#### New Endpoints:
1. **`GET /api/timetable/today?username={username}`**
   - Get today's schedule for a specific user
   - Returns both regular and special schedules for today

2. **`GET /api/timetable/week?username={username}`**
   - Get the next 7 days of schedules
   - Perfect for weekly planning

3. **`GET /api/timetable/upcoming?username={username}&days={days}`**
   - Get upcoming schedules for next N days (1-30)
   - Flexible schedule preview

4. **`GET /api/timetable/special/future?username={username}`**
   - Get all future special events
   - Never miss an important special class or event

5. **`GET /api/timetable/stats?username={username}`**
   - Get statistics about user's timetable
   - Returns:
     - Total schedules
     - Regular schedules count
     - Special schedules count
     - Future special schedules count
     - Classes today count

6. **`GET /api/timetable/check-date?username={username}&date={date}`**
   - Check if user has classes on a specific date
   - Returns boolean and date information

### 3. **Improved Frontend UI** 🎨

#### Statistics Dashboard
- **Visual Overview**: 4 stat cards showing:
  - 📅 Classes Today
  - 🔄 Regular Schedules
  - ⭐ Upcoming Special Events
  - 📊 Total Schedules
- **Real-time Updates**: Stats refresh after every add/edit/delete operation
- **Beautiful Design**: Gradient backgrounds and hover effects

#### Enhanced View Modes
1. **Today View** (Default)
   - Shows only today's classes
   - Badge shows count of classes today
   - Most frequently used view

2. **This Week View**
   - Shows all classes for the next 7 days
   - Perfect for weekly planning
   - Includes both regular and special schedules

3. **Special Events View**
   - Shows only future special schedules
   - Helps track one-time events
   - Useful for exams, guest lectures, etc.

4. **All Schedules View**
   - Shows all user's schedules
   - Complete overview

5. **Day Selector**
   - View schedules for any specific day of the week
   - Filter regular schedules by day

#### UI Improvements
- **Better Button Styling**: Active state clearly visible with gradient backgrounds
- **Visual Indicators**: Different colors for regular vs special schedules
- **Responsive Design**: Works well on all screen sizes
- **Loading States**: Clear feedback during operations
- **Error Handling**: User-friendly error messages

### 4. **Schedule Management Features** 📅

#### Regular Schedules
- Create weekly recurring classes
- Specify day of week, time, location, and lecturer
- Automatically appear every week on the specified day

#### Special Schedules
- One-time events for specific dates
- Perfect for:
  - Guest lectures
  - Special exams
  - Lab sessions
  - Field trips
  - Makeup classes
- Include optional descriptions
- Day of week is automatically calculated from the date

#### CRUD Operations
- **Create**: Add new regular or special schedules
- **Read**: View schedules in multiple formats (today, week, by day, all)
- **Update**: Edit existing schedules
- **Delete**: Remove schedules with confirmation

### 5. **Smart Scheduling Service** 🤖

#### Daily Email Job
- **Runs**: Every day at 7:00 AM
- **Process**:
  1. Fetches all registered users
  2. Gets each user's schedule for today
  3. Sends personalized HTML email
  4. Logs success/failure for monitoring

#### Reminder System
- **Runs**: Every hour
- **Process**:
  1. Checks for classes starting in the next hour
  2. Sends reminder emails to users
  3. Includes class details and location

## 📁 File Structure

### Backend Changes
```
sem-tracker/
├── src/main/java/com/kops/sem_tracker/
│   ├── SemTrackerApplication.java (Added @EnableScheduling)
│   ├── service/
│   │   ├── EmailService.java (Enhanced with HTML templates)
│   │   ├── DailyScheduleEmailService.java (NEW - Scheduled email jobs)
│   │   └── TimeTableService.java (Added user-specific methods)
│   └── controller/
│       └── TimeTableController.java (Added new endpoints)
├── src/main/resources/
│   └── application.properties (Added email configuration)
└── pom.xml (Added spring-boot-starter-mail dependency)
```

### Frontend Changes
```
frontend_SEM_track/
├── src/
│   ├── Pages/
│   │   └── timetablepage.tsx (Enhanced UI with stats dashboard)
│   ├── Styles/
│   │   └── timetablepage.css (Added stats and button styles)
│   └── config/
│       └── apiConfig.ts (Added new API endpoints)
```

## 🚀 Setup Instructions

### 1. Backend Setup

#### Install Dependencies
```bash
cd SEM_full2/sem-tracker
mvn clean install
```

#### Configure Email (Optional)
To enable email notifications, set these environment variables or update `application.properties`:

```bash
# For Gmail (requires App Password)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@semtracker.com
EMAIL_ENABLED=true
DAILY_EMAIL_ENABLED=true
```

**Note**: If you don't configure email, the system will log schedule previews to the console instead.

#### How to Get Gmail App Password:
1. Go to Google Account settings
2. Enable 2-Factor Authentication
3. Go to Security > 2-Step Verification > App passwords
4. Create new app password for "Mail"
5. Use that password in EMAIL_PASSWORD

### 2. Frontend Setup

```bash
cd SEM_full2/frontend_SEM_track
npm install
npm run dev
```

### 3. Database
No schema changes required! The existing database structure supports all new features.

## 🎯 Usage Guide

### For Students

#### Daily Routine
1. **Morning**: Check your email for today's schedule (sent automatically at 7:00 AM)
2. **Website**: Visit the timetable page to see real-time updates
3. **Planning**: Use "This Week" view to plan ahead

#### Adding Classes
1. Click "➕ Add Schedule"
2. Choose schedule type:
   - **Regular**: For weekly recurring classes
   - **Special**: For one-time events
3. Fill in details (subject, time, location, lecturer)
4. Click "Add"

#### Managing Schedules
- **Edit**: Click "Edit" button on any schedule
- **Delete**: Click "Delete" button (with confirmation)
- **Filter**: Use view buttons to filter schedules

#### View Modes
- **📅 Today**: See what's scheduled for today
- **📆 This Week**: Plan your week ahead
- **⭐ Special Events**: View upcoming special schedules
- **📋 All Schedules**: See everything
- **📌 Select Day**: View schedules for a specific weekday

### For Administrators

#### Monitoring Email Service
Check application logs for email job status:
```
[DailyScheduleEmailService] Starting daily schedule email job
[DailyScheduleEmailService] Sent schedule to username (3 classes)
[DailyScheduleEmailService] Daily schedule email job completed
```

#### Configuring Schedule Times
Edit `application.properties`:
```properties
# Change email send time (24-hour format)
app.schedule.daily-email.time=07:00
```

Or modify `DailyScheduleEmailService.java`:
```java
@Scheduled(cron = "0 0 7 * * *") // 7:00 AM
@Scheduled(cron = "0 0 8 * * *") // 8:00 AM
@Scheduled(cron = "0 30 6 * * *") // 6:30 AM
```

## 🔧 Technical Details

### Email Service Architecture
```
┌─────────────────────────────────────┐
│  DailyScheduleEmailService          │
│  (Scheduled Job)                    │
└─────────────┬───────────────────────┘
              │
              ├──> TimeTableService
              │    (Get user schedules)
              │
              ├──> StudentRepository
              │    (Get user emails)
              │
              └──> EmailService
                   (Send HTML emails)
```

### Data Flow
```
User Action (Add/Edit Schedule)
    ↓
TimeTableController
    ↓
TimeTableService
    ↓
TimeTableRepository (Save to DB)
    ↓
Frontend (Update UI + Stats)

Daily at 7:00 AM:
Scheduler Trigger
    ↓
DailyScheduleEmailService
    ↓
Fetch Today's Schedules
    ↓
Build HTML Email
    ↓
Send to All Users
```

### API Response Examples

#### Today's Schedule
```json
GET /api/timetable/today?username=john_doe

[
  {
    "id": 1,
    "subject": "Data Structures",
    "day": "MONDAY",
    "startTime": "09:00:00",
    "endTime": "11:00:00",
    "location": "Room 101",
    "lecturer": "Dr. Smith",
    "isSpecialSchedule": false,
    "username": "john_doe"
  },
  {
    "id": 15,
    "subject": "Guest Lecture - AI",
    "specialDate": "2026-01-28",
    "day": "TUESDAY",
    "startTime": "14:00:00",
    "endTime": "16:00:00",
    "location": "Auditorium",
    "lecturer": "Prof. Johnson",
    "description": "Special seminar on AI trends",
    "isSpecialSchedule": true,
    "username": "john_doe"
  }
]
```

#### Statistics
```json
GET /api/timetable/stats?username=john_doe

{
  "totalSchedules": 25,
  "regularSchedules": 20,
  "specialSchedules": 5,
  "futureSpecialSchedules": 3,
  "classesToday": 4
}
```

## 🎨 UI Screenshots Description

### Statistics Dashboard
- 4 cards showing key metrics
- Gradient backgrounds matching brand colors
- Hover effects for interactivity
- Real-time updates

### View Buttons
- Clear active state with purple gradient
- Icons for easy identification
- Responsive layout
- Day selector dropdown

### Schedule Table
- Clean, modern design
- Special schedules highlighted in yellow
- Action buttons for edit/delete
- Responsive columns

## 🔐 Security Considerations

1. **Email Configuration**: Store credentials in environment variables, not in code
2. **User Authentication**: All endpoints should verify user identity
3. **Data Validation**: Backend validates all schedule inputs
4. **SQL Injection**: Using JPA prevents SQL injection
5. **XSS Protection**: HTML content is escaped in emails

## 📊 Performance Optimization

1. **Database Queries**: Optimized with indexes on username and date fields
2. **Email Sending**: Async processing to avoid blocking
3. **Frontend Caching**: Stats and schedules cached between refreshes
4. **Lazy Loading**: Only fetch data for current view mode

## 🐛 Troubleshooting

### Email Not Sending
**Problem**: Email configuration not working
**Solution**:
1. Check `EMAIL_ENABLED=true` in properties
2. Verify Gmail App Password is correct
3. Check firewall allows port 587
4. Review logs for specific errors

### Schedule Not Appearing
**Problem**: Schedule added but not showing
**Solution**:
1. Check username is set correctly
2. Verify view mode is appropriate (today/week/special)
3. Check date/day is correct
4. Refresh the page

### Stats Not Updating
**Problem**: Statistics dashboard shows old data
**Solution**:
1. Stats refresh after operations
2. Try hard refresh (Ctrl+F5)
3. Check browser console for errors

## 🚀 Future Enhancements

Possible future improvements:
1. **Calendar Integration**: Export to Google Calendar/iCal
2. **Mobile App**: Native iOS/Android apps
3. **Recurring Patterns**: Bi-weekly classes, custom patterns
4. **Conflict Detection**: Warn about overlapping schedules
5. **Attendance Integration**: Link with attendance tracking
6. **Push Notifications**: Real-time mobile notifications
7. **Dark Mode**: UI theme toggle
8. **Multi-language**: Support for multiple languages
9. **Share Schedules**: Share schedules with classmates
10. **Print View**: Printer-friendly schedule view

## 📝 Changelog

### Version 2.0 (Current)
- ✅ Added daily email notifications
- ✅ Enhanced backend APIs with user-specific endpoints
- ✅ Implemented statistics dashboard
- ✅ Improved UI with better view modes
- ✅ Added week view and future special schedules
- ✅ Created scheduled email jobs
- ✅ Enhanced CSS styling

### Version 1.0 (Previous)
- Basic CRUD operations
- Regular and special schedules
- Single table view
- Basic filtering

## 🤝 Contributing

To contribute to this feature:
1. Follow the existing code structure
2. Add comprehensive tests
3. Update documentation
4. Follow naming conventions
5. Add comments for complex logic

## 📞 Support

For issues or questions:
1. Check this documentation first
2. Review application logs
3. Check browser console for frontend errors
4. Contact the development team

---

**Last Updated**: January 28, 2026
**Version**: 2.0
**Maintainer**: Development Team
