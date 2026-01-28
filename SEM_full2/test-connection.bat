@echo off
REM Quick Test Script for Backend Connection and Build Performance

echo ====================================================
echo Backend Connection and Build Performance Test
echo ====================================================
echo.

cd /d E:\Sem_tracker_new\SEM-Tracker\SEM_full2

echo [1/6] Stopping existing containers...
docker compose down -v > nul 2>&1

echo [2/6] Cleaning old images...
docker rmi backend_new frontend_new > nul 2>&1

echo [3/6] Building services (this will take 5-6 minutes first time)...
echo       Next builds will only take 30 seconds!
docker compose build

echo.
echo [4/6] Starting services...
docker compose up -d

echo.
echo [5/6] Waiting for services to be healthy (90 seconds)...
timeout /t 90 /nobreak > nul

echo.
echo [6/6] Testing connectivity...
echo.

echo MySQL:
docker exec mysql_container mysqladmin ping -h localhost -u root -pKopu2001 > nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] MySQL is healthy
) else (
    echo   [FAIL] MySQL is not responding
)

echo.
echo Backend:
curl -s http://localhost:8081/actuator/health | findstr "UP" > nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Backend is healthy and API is ready
) else (
    echo   [FAIL] Backend is not responding - check logs: docker compose logs backend
)

echo.
echo Frontend:
curl -s http://localhost:5173 > nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Frontend is running
) else (
    echo   [FAIL] Frontend is not responding
)

echo.
echo ====================================================
echo Test Complete
echo ====================================================
echo.
echo Access your application:
echo   Frontend: http://localhost:5173
echo   Backend:  http://localhost:8081
echo   Health:   http://localhost:8081/actuator/health
echo.
echo To view logs:
echo   docker compose logs -f backend
echo   docker compose logs -f frontend
echo.

docker compose ps

pause
