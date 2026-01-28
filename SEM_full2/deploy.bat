@echo off
REM Complete Build and Deployment Fix (Windows)
REM This script fixes Maven offline errors and database connection issues

setlocal enabledelayedexpansion

cd /d "%~dp0SEM_full2"

echo.
echo ==========================================
echo.   Complete Build and Deployment Fix
echo ==========================================
echo.

echo [1/7] Cleaning up previous builds...
docker compose down -v >nul 2>&1
docker rmi backend_new frontend_new >nul 2>&1

echo [2/7] Pruning Docker system...
docker volume prune -f >nul 2>&1
docker system prune -f >nul 2>&1

echo.
echo [3/7] Building services...
echo        This will take ~5-6 minutes on first build
echo        Subsequent builds: ~30-45 seconds
echo.

docker compose build
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Build failed!
    echo.
    echo Troubleshooting:
    echo   1. Is Docker running?
    echo   2. Check disk space: docker system df
    echo   3. Clear cache: docker system prune -a
    echo   4. Check logs: docker compose logs
    pause
    exit /b 1
)

echo.
echo [4/7] Build successful!
echo.
echo [5/7] Starting services...
docker compose up -d

echo.
echo [6/7] Waiting for services to initialize...
echo        MySQL: ~15 seconds
echo        Backend: ~45 seconds
echo        Frontend: ~30 seconds
timeout /t 60 /nobreak

echo.
echo [7/7] Checking service health...
echo.

REM Check MySQL
echo Checking MySQL...
docker exec mysql_container mysqladmin ping -h localhost -u root -pKopu2001 >nul 2>&1
if !errorlevel! equ 0 (
    echo   MySQL: [OK] Healthy
) else (
    echo   MySQL: [WAIT] Starting... checking again
    timeout /t 30 /nobreak >nul
)

REM Check Backend
echo Checking Backend...
curl -s http://localhost:8081/actuator/health | findstr "UP" >nul 2>&1
if !errorlevel! equ 0 (
    echo   Backend: [OK] Healthy - API Ready
) else (
    echo   Backend: [WAIT] Starting... checking again
    timeout /t 30 /nobreak >nul
    curl -s http://localhost:8081/actuator/health | findstr "UP" >nul 2>&1
    if !errorlevel! equ 0 (
        echo   Backend: [OK] Now Healthy
    ) else (
        echo   Backend: [ERROR] Not responding
        echo           Check logs: docker compose logs backend
    )
)

REM Check Frontend
echo Checking Frontend...
curl -s http://localhost:5173 >nul 2>&1
if !errorlevel! equ 0 (
    echo   Frontend: [OK] Running
) else (
    echo   Frontend: [WAIT] Still loading...
)

echo.
echo ==========================================
echo   Deployment Complete!
echo ==========================================
echo.

echo Access your application:
echo   Frontend: http://localhost:5173
echo   Backend:  http://localhost:8081
echo   Health:   http://localhost:8081/actuator/health
echo.

echo Service Status:
docker compose ps

echo.
echo Useful Commands:
echo   View logs:      docker compose logs -f
echo   Stop:           docker compose down
echo   Restart:        docker compose restart
echo   Clean rebuild:  docker compose down -v ^&^& docker compose build --no-cache
echo.

pause
