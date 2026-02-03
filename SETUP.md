# Installation & Setup Guide

Complete step-by-step guide to get SEM-Tracker running on your machine.

## 📋 Prerequisites

Before starting, ensure you have installed:

### Required
- **Node.js** v16+ ([Download](https://nodejs.org/))
- **Java JDK** 11+ ([Download](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html))
- **Maven** 3.6+ ([Download](https://maven.apache.org/download.cgi))
- **MySQL** 8.0+ ([Download](https://dev.mysql.com/downloads/mysql/)) OR **Docker** & **Docker Compose**

### Optional (For Production)
- **Git** ([Download](https://git-scm.com/))
- **Docker** & **Docker Compose** (Alternative to local MySQL)

## ✅ Verify Installations

```bash
# Check Node.js
node --version      # Should be v16+

# Check Java
java -version       # Should be 11+

# Check Maven
mvn --version       # Should be 3.6+

# Check MySQL (if installed locally)
mysql --version     # Should be 8.0+
```

## 🗄️ Database Setup

### Option 1: Local MySQL

**Windows**
1. Install MySQL from the official installer
2. Run MySQL server
3. Create database:
```sql
CREATE DATABASE sem_db;
CREATE USER 'sem_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON sem_db.* TO 'sem_user'@'localhost';
FLUSH PRIVILEGES;
```

**Linux/Mac**
```bash
# Install via Homebrew (Mac)
brew install mysql

# Start service
brew services start mysql

# Secure installation
mysql_secure_installation

# Create database
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE sem_db;
CREATE USER 'sem_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON sem_db.* TO 'sem_user'@'localhost';
FLUSH PRIVILEGES;
```

### Option 2: Docker (Recommended)

No local MySQL needed! Docker handles it.

```bash
cd SEM_full2
docker-compose up -d mysql
```

The `docker-compose.yml` automatically creates:
- Database: `sem_db`
- User: `root` with password `root123`
- Port: 3306

## ⚙️ Backend Setup

### Step 1: Navigate to Backend Directory

```bash
cd SEM_full2/sem-tracker
```

### Step 2: Configure Application

Edit `src/main/resources/application.properties`:

**For Local MySQL**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/sem_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=sem_user
spring.datasource.password=password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server.port=8081
spring.application.name=sem-tracker

# Email Configuration (Optional)
app.email.enabled=false
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${EMAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Schedule Configuration
app.schedule.daily-email.time=07:00
app.schedule.daily-email.enabled=true
```

**For Docker MySQL**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://mysql:3306/sem_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# [Rest remains same as above]
```

### Step 3: Install Dependencies

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Create the JAR file

### Step 4: Run Backend

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR file
java -jar target/sem-tracker-0.0.1-SNAPSHOT.jar
```

✅ Backend running: `http://localhost:8081`

**Verify it's working:**
```bash
curl http://localhost:8081/api/timetable
```

## 🎨 Frontend Setup

### Step 1: Navigate to Frontend Directory

```bash
cd SEM_full2/frontend_SEM_track
```

### Step 2: Install Dependencies

```bash
npm install
```

This installs all required npm packages.

### Step 3: Configure API Endpoint

Edit `src/config/apiConfig.ts`:

```typescript
// For local development
const API_URL = 'http://localhost:8081/api';

export default API_URL;
```

### Step 4: Run Development Server

```bash
npm run dev
```

✅ Frontend running: `http://localhost:5173`

**You should see:**
```
VITE v4.x.x  ready in 234 ms

➜  Local:   http://localhost:5173/
➜  press h to show help
```

## 🌐 Accessing the Application

Open your browser and navigate to:
```
http://localhost:5173
```

### First Time
1. **Login/Sign up** with credentials
2. **Navigate to Timetable** page
3. **Add a schedule** to get started

## 🚀 Running Both Backend & Frontend

**Terminal 1 - Backend**
```bash
cd SEM_full2/sem-tracker
mvn spring-boot:run
```

**Terminal 2 - Frontend**
```bash
cd SEM_full2/frontend_SEM_track
npm run dev
```

Both will run simultaneously.

## 🐳 Docker Setup (All-in-One)

Run entire application with Docker:

```bash
# Navigate to project root
cd SEM_full2

# Build and start all services
docker-compose up --build

# OR for background mode
docker-compose up -d --build
```

**Services started:**
- MySQL: `localhost:3306`
- Backend: `http://localhost:8081`
- Frontend: `http://localhost:5173`

**Access the application:**
```
http://localhost:5173
```

**Stop services:**
```bash
docker-compose down
```

**View logs:**
```bash
docker-compose logs -f backend   # Backend logs
docker-compose logs -f frontend  # Frontend logs
docker-compose logs -f mysql     # Database logs
```

## 📧 Email Setup (Optional)

### For Gmail Users

**Step 1: Generate App Password**
1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Factor Authentication (if not already enabled)
3. Create App password for "Mail"
4. Copy the 16-character password

**Step 2: Configure Backend**

Edit `application.properties`:
```properties
app.email.enabled=true
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
app.email.from=noreply@semtracker.com
```

OR use environment variables:

**Windows (PowerShell)**
```powershell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password"
$env:EMAIL_ENABLED="true"
mvn spring-boot:run
```

**Linux/Mac**
```bash
export EMAIL_USERNAME="your-email@gmail.com"
export EMAIL_PASSWORD="your-app-password"
export EMAIL_ENABLED="true"
mvn spring-boot:run
```

**Step 3: Verify Email Works**
- Check console logs at 7:00 AM
- Look for "Sending daily schedule email" messages
- Check recipient email inbox

### For Other Email Providers
Modify `application.properties`:
```properties
spring.mail.host=your-smtp-host     # e.g., smtp.outlook.com
spring.mail.port=587                # Usually 587 or 465
spring.mail.username=your-email
spring.mail.password=your-password
```

## 🧪 Testing the Application

### Test Checklist

- [ ] **View Modes**
  - [ ] Click "Today" button
  - [ ] Click "This Week" button
  - [ ] Click "Special Events" button
  - [ ] Click "All Schedules" button
  - [ ] Select a day from dropdown

- [ ] **Add Schedule**
  - [ ] Click "Add Schedule"
  - [ ] Add regular weekly schedule
  - [ ] Add special one-time schedule
  - [ ] Verify it appears in all views

- [ ] **Edit Schedule**
  - [ ] Click edit button on a schedule
  - [ ] Modify details
  - [ ] Verify changes saved

- [ ] **Delete Schedule**
  - [ ] Click delete button
  - [ ] Confirm deletion
  - [ ] Verify it's removed

- [ ] **Statistics**
  - [ ] Verify all 4 cards show
  - [ ] Add a schedule and verify numbers update
  - [ ] Delete a schedule and verify numbers update

- [ ] **Email** (if enabled)
  - [ ] Check console at 7:00 AM
  - [ ] Verify email received
  - [ ] Check email formatting

### Sample Test Data

**Regular Schedule**
```
Subject: Data Structures
Day: Monday
Start Time: 09:00
End Time: 11:00
Location: Room 101
Lecturer: Dr. Smith
```

**Special Schedule**
```
Subject: Guest Lecture
Date: Next Friday
Start Time: 14:00
End Time: 16:00
Location: Auditorium
Description: AI Trends Seminar
```

## 🔧 Troubleshooting

### Backend Issues

**Port 8081 Already in Use**
```bash
# Find the process using port 8081
netstat -ano | findstr :8081

# Kill the process (Windows)
taskkill /PID <process-id> /F

# Or change port in application.properties
server.port=8082
```

**MySQL Connection Failed**
```
Error: Communications link failure
```
**Solution:**
1. Verify MySQL is running
2. Check credentials in `application.properties`
3. Ensure database exists: `CREATE DATABASE sem_db;`

**Maven Build Fails**
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn install -U

# Check Java version
java -version  # Should be 11+
```

**Dependency Download Issues**
```bash
# Use offline mode if internet is slow
mvn clean install -o

# Or force update from repository
mvn clean install -U
```

### Frontend Issues

**Port 5173 Already in Use**
```bash
# Kill the process or specify different port
npm run dev -- --port 3000
```

**Node Modules Issues**
```bash
# Clean and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

**API Connection Error**
```
Error: Cannot reach API at http://localhost:8081
```
**Solution:**
1. Ensure backend is running
2. Check `apiConfig.ts` has correct URL
3. Verify browser console (F12) for CORS errors

### Database Issues

**Cannot Create Database**
```sql
-- Check if database exists
SHOW DATABASES;

-- Drop and recreate
DROP DATABASE sem_db;
CREATE DATABASE sem_db;
```

**Tables Not Created**
- Ensure `spring.jpa.hibernate.ddl-auto=update` is set
- Check application logs for Hibernate errors
- Verify JPA/Hibernate configuration

### Docker Issues

**Container Won't Start**
```bash
# Check logs
docker-compose logs backend

# Rebuild without cache
docker-compose build --no-cache
```

**Port Already in Use**
```bash
# Stop all containers
docker-compose down

# Check port status
docker ps -a
```

## 🔍 Development Tools

### Useful Commands

**Backend**
```bash
# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc

# Check code quality
mvn checkstyle:check
```

**Frontend**
```bash
# Build for production
npm run build

# Preview production build
npm run preview

# Format code
npm run format

# Lint check
npm run lint
```

### Browser DevTools

Press `F12` to open Developer Console:
- **Network tab**: See API requests
- **Console tab**: View JavaScript errors
- **Storage tab**: Check localStorage data

### MySQL Client

```bash
# Connect to MySQL
mysql -u root -p

# Or with Docker
docker exec -it sem_mysql mysql -u root -p

# Useful queries
USE sem_db;
SHOW TABLES;
SELECT * FROM time_table;
```

## 📊 Performance Optimization

### Frontend Build
```bash
# Production build (optimized)
npm run build

# Check bundle size
npm run build -- --analyze
```

### Backend Performance
```bash
# Run with profiling
mvn spring-boot:run -Drun.jvmArguments="-XX:+PrintGCDetails"
```

## 🚀 Deployment

### Production Build

**Frontend**
```bash
npm run build
# Creates optimized build in dist/ folder
```

**Backend**
```bash
mvn clean package
# Creates JAR in target/ folder
```

### Docker Deployment
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 📚 Additional Resources

- **Spring Boot**: [Official Docs](https://spring.io/projects/spring-boot)
- **React**: [Official Docs](https://react.dev)
- **Maven**: [Official Docs](https://maven.apache.org)
- **Docker**: [Official Docs](https://docs.docker.com)
- **MySQL**: [Official Docs](https://dev.mysql.com/doc/)

## ✅ Success Checklist

- [ ] All prerequisites installed and verified
- [ ] Database created (MySQL or Docker)
- [ ] Backend running on port 8081
- [ ] Frontend running on port 5173
- [ ] Can login to application
- [ ] Timetable page loads
- [ ] Can add a schedule
- [ ] Can view different modes
- [ ] Statistics dashboard displays
- [ ] Email configured (optional)

## 🎓 Next Steps

1. **Explore Features**: Try all view modes and operations
2. **Customize**: Modify styles and configurations
3. **Extend**: Add new features as needed
4. **Deploy**: Push to production when ready
5. **Monitor**: Check logs and metrics

---

**Setup Time**: 10-15 minutes  
**With Docker**: 5 minutes  
**Ready to code**: ✅

Happy coding! 🚀
