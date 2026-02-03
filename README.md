# SEM-Tracker

A modern web application for efficient semester management and scheduling. Students can view, manage, and plan their academic schedules with automated email notifications and real-time statistics.

## � Live Demo

**🚀 [View Live Application](http://65.2.6.115)** - Deployed on AWS EC2

Try it out! The application is live and running. You can explore all features including schedule management, multiple view modes, and the statistics dashboard.

## �🎯 Project Overview

SEM-Tracker is a full-stack web application designed to help students organize their semester effectively. It provides intuitive schedule management, multiple view modes, automated notifications, and a comprehensive dashboard for schedule overview.

### Key Features

✅ **Schedule Management**
- Create regular weekly schedules and special one-time events
- Easy add, edit, and delete operations
- Automatic day calculation for special dates

✅ **Multiple View Modes**
- **📅 Today**: See today's classes only
- **📆 This Week**: Plan the next 7 days
- **⭐ Special Events**: View all upcoming special schedules
- **📋 All Schedules**: Complete overview
- **📌 Day Selector**: Filter by specific weekday

✅ **Statistics Dashboard**
- Classes Today count
- Regular Schedules total
- Upcoming Special Events count
- Total Schedules overview
- Real-time updates after every action

✅ **Automated Email Notifications**
- Daily schedule emails at 7:00 AM
- Beautiful HTML-formatted content
- Personalized for each user
- Includes regular classes and special events
- Configurable via environment variables

✅ **Modern User Interface**
- Responsive design
- Gradient cards and smooth animations
- Intuitive navigation
- Color-coded schedules
- Mobile-friendly layout

✅ **Comprehensive REST API**
- 11 API endpoints for flexible data access
- User-specific schedule retrieval
- Week and upcoming schedules endpoints
- Statistics data endpoint
- Full CRUD operations

## 🏗️ Tech Stack

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Database**: MySQL
- **Email**: Spring Mail (SMTP)
- **Scheduler**: Spring @Scheduled
- **Build Tool**: Maven

### Frontend
- **Framework**: React
- **Language**: TypeScript
- **Build Tool**: Vite
- **Styling**: CSS3 with modern features
- **Package Manager**: npm

### Deployment
- **Containerization**: Docker & Docker Compose
- **CI/CD**: Jenkins
- **Cloud**: AWS (optional)

## 📁 Project Structure

```
SEM-Tracker/
├── SEM_full2/
│   ├── sem-tracker/               # Backend (Spring Boot)
│   │   ├── src/main/java/         # Java source code
│   │   ├── src/main/resources/    # Configuration files
│   │   ├── pom.xml                # Maven dependencies
│   │   ├── Dockerfile             # Docker image
│   │   └── docker-compose.yml     # Service orchestration
│   │
│   └── frontend_SEM_track/        # Frontend (React)
│       ├── src/
│       │   ├── Pages/             # Page components
│       │   ├── Components/        # Reusable components
│       │   ├── Styles/            # CSS files
│       │   └── config/            # API configuration
│       ├── package.json           # npm dependencies
│       ├── Dockerfile             # Docker image
│       └── vite.config.ts         # Vite configuration
│
├── README.md                      # This file
├── SETUP.md                       # Installation & setup guide
├── Jenkinsfile                    # CI/CD pipeline
└── LICENSE                        # Project license
```

## 🚀 Quick Start (5 minutes)

### Prerequisites
- Node.js (v16+)
- Java JDK 11+
- Maven 3.6+
- MySQL 8.0+ (or Docker)

### Running Locally

**1. Start Backend**
```bash
cd SEM_full2/sem-tracker
mvn spring-boot:run
```
Backend: http://localhost:8081

**2. Start Frontend**
```bash
cd SEM_full2/frontend_SEM_track
npm install
npm run dev
```
Frontend: http://localhost:5173

**3. Login & Explore**
- Navigate to http://localhost:5173
- Login with your credentials
- Explore timetable features

> For detailed setup instructions, see [SETUP.md](SETUP.md)

## 📋 Features in Detail

### 1. Schedule Management
- **Regular Schedules**: Recurring classes on specific days
- **Special Schedules**: One-time events on specific dates
- **Quick Add**: 30-second schedule creation
- **Edit & Delete**: Manage existing schedules easily
- **Validation**: Smart form validation

### 2. View Modes
All views update automatically based on your data:
- **Today**: Only shows today's classes
- **This Week**: Shows next 7 days
- **Special Events**: Only future special schedules
- **All Schedules**: Everything in one view
- **Day Filter**: Shows specific weekday classes

### 3. Statistics
Real-time metrics that update after every action:
- Classes scheduled for today
- Total regular schedules
- Upcoming special events
- Complete schedule count

### 4. Email Notifications
**Automated Schedule Emails**
- Sent daily at 7:00 AM
- Beautiful HTML format
- Shows all classes for the day
- Includes time, location, lecturer, description
- Friendly message on free days
- Configurable via environment variables

## 🔧 Configuration

### Email Setup (Optional)
Set environment variables for Gmail:

**Windows (PowerShell)**
```powershell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password"
$env:EMAIL_ENABLED="true"
```

**Linux/Mac**
```bash
export EMAIL_USERNAME="your-email@gmail.com"
export EMAIL_PASSWORD="your-app-password"
export EMAIL_ENABLED="true"
```

Or edit `application.properties`:
```properties
app.email.enabled=true
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.schedule.daily-email.time=07:00
```

> Email is disabled by default. All features work without it.

### Database Configuration
Default connection (in `application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sem_db
spring.datasource.username=root
spring.datasource.password=your_password
```

## 🐳 Docker Deployment

### Build & Run with Docker Compose
```bash
cd SEM_full2
docker-compose up --build
```

This starts:
- MySQL database on port 3306
- Spring Boot backend on port 8081
- React frontend on port 5173

### Access the Application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8081/api
- Database: localhost:3306

## 📊 API Endpoints

### Schedule Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/timetable` | Get all schedules |
| GET | `/api/timetable/today` | Get today's schedules |
| GET | `/api/timetable/week` | Get next 7 days |
| GET | `/api/timetable/upcoming` | Get upcoming special events |
| POST | `/api/timetable` | Create schedule |
| PUT | `/api/timetable/{id}` | Update schedule |
| DELETE | `/api/timetable/{id}` | Delete schedule |

### Statistics Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/timetable/stats` | Get statistics data |
| GET | `/api/timetable/stats/today` | Get today's class count |

## 🧪 Testing

### Test the Features
1. **View Modes**: Switch between Today, Week, Special, All, and Day views
2. **Add Schedule**: Create regular and special schedules
3. **Edit**: Update existing schedules
4. **Delete**: Remove schedules (with confirmation)
5. **Statistics**: Verify real-time updates
6. **Email** (if enabled): Check scheduled daily emails

### Sample Test Data
**Regular Schedule**
- Subject: Data Structures
- Day: Monday
- Time: 09:00 - 11:00
- Location: Room 101
- Lecturer: Dr. Smith

**Special Schedule**
- Subject: Guest Lecture
- Date: Next Friday
- Time: 14:00 - 16:00
- Location: Auditorium

## 🚨 Troubleshooting

### Backend Won't Start
```bash
# Check if port 8081 is in use
netstat -ano | findstr :8081

# Kill the process or change port in application.properties
server.port=8082
```

### Frontend Won't Start
```bash
# Clear and reinstall dependencies
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Database Connection Issues
```bash
# Verify MySQL is running
# Update connection in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/sem_db
```

### Emails Not Sending
1. Verify `app.email.enabled=true`
2. Check Gmail App Password is correct
3. Review console logs for errors
4. Ensure port 587 is accessible

## 📚 Documentation

- **[SETUP.md](SETUP.md)** - Detailed setup and installation guide
- **Code Comments** - Throughout the codebase
- **API Documentation** - In controller classes

## 🔐 Security

- User credentials via environment variables
- User-specific data isolation
- Input validation on all endpoints
- SQL injection prevention (JPA)
- XSS protection in HTML emails
- Secure password handling

## 📈 Performance

### Build & Deploy Times
| Scenario | Time |
|----------|------|
| First build | 5-6 min |
| Rebuild (code change) | ~30 sec |
| Rebuild (pom.xml change) | ~2 min |

### Backend Response Times
- GET requests: < 100ms
- POST/PUT requests: < 200ms
- Database queries: < 50ms

## 👥 Contributing

Contributions are welcome! Please follow these guidelines:
1. Create a feature branch
2. Make your changes
3. Add tests if applicable
4. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙋 Support & Issues

For bug reports and feature requests:
1. Check existing issues first
2. Provide detailed description
3. Include steps to reproduce
4. Share error messages and logs

## 🎓 Academic Use

This project is designed for educational purposes. It's perfect for:
- Computer Science students
- Web development courses
- Full-stack application examples
- Spring Boot learning
- React learning

## 🚀 Getting Started

1. **Install dependencies**: `npm install` (frontend) & `mvn install` (backend)
2. **Configure database**: Update `application.properties`
3. **Run application**: Follow [Quick Start](#-quick-start-5-minutes) section
4. **Explore features**: Check out all view modes and functionality
5. **Set up email** (optional): Follow [Configuration](#-configuration) section

## 📞 Contact

For questions or suggestions, please open an issue in the repository.

---

**Version**: 1.0.0  
**Last Updated**: February 2026  
**Status**: Production Ready ✅

Enjoy efficient semester management with SEM-Tracker! 🎓✨
