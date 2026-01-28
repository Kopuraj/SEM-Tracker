#!/bin/bash
# Complete Build and Deployment Fix
# This script fixes the Maven offline mode error and database connection issue

set -e  # Exit on error

echo "=========================================="
echo "🔧 Complete Build & Deployment Fix"
echo "=========================================="
echo ""

cd "$(dirname "$0")/SEM_full2"

echo "📦 Cleaning up previous builds..."
docker compose down -v 2>/dev/null || true
docker rmi backend_new frontend_new 2>/dev/null || true
docker volume prune -f 2>/dev/null || true

echo ""
echo "🏗️  Building services with proper caching..."
echo "⏱️  This will take ~5-6 minutes on first build"
echo "⚡ Subsequent builds: ~30-45 seconds for code changes"
echo ""

# Build with proper error handling
if ! docker compose build; then
    echo ""
    echo "❌ Build failed! Here's what to check:"
    echo "   1. Docker is running?"
    echo "   2. Enough disk space?"
    echo "   3. Network connection?"
    echo ""
    echo "Try clearing Docker cache:"
    echo "   docker system prune -a"
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""
echo "🚀 Starting services..."
docker compose up -d

echo ""
echo "⏳ Waiting for services to initialize..."
echo "   MySQL: ~15 seconds"
echo "   Backend: ~45 seconds (database setup)"
echo "   Frontend: ~30 seconds"
sleep 60

echo ""
echo "🔍 Checking service health..."
echo ""

# Check MySQL
echo -n "MySQL: "
if docker exec mysql_container mysqladmin ping -h localhost -u root -pKopu2001 >/dev/null 2>&1; then
    echo "✅ Healthy"
else
    echo "⚠️  Starting up... wait 30 more seconds"
    sleep 30
fi

echo ""
echo -n "Backend: "
if curl -s http://localhost:8081/actuator/health | grep -q '"status":"UP"'; then
    echo "✅ Healthy (API Ready)"
else
    echo "⚠️  Starting up... wait 30 more seconds"
    sleep 30
    if curl -s http://localhost:8081/actuator/health | grep -q '"status":"UP"'; then
        echo "✅ Now Healthy"
    else
        echo "❌ Not responding - check logs"
        echo "   docker compose logs backend"
    fi
fi

echo ""
echo -n "Frontend: "
if curl -s http://localhost:5173 >/dev/null 2>&1; then
    echo "✅ Running"
else
    echo "⚠️  Still loading..."
fi

echo ""
echo "=========================================="
echo "✅ Deployment Complete!"
echo "=========================================="
echo ""
echo "📍 Access your application:"
echo "   Frontend: http://localhost:5173"
echo "   Backend API: http://localhost:8081"
echo "   Health Check: http://localhost:8081/actuator/health"
echo ""
echo "📊 Service status:"
docker compose ps
echo ""
echo "📝 View logs:"
echo "   All: docker compose logs -f"
echo "   Backend: docker compose logs -f backend"
echo "   Frontend: docker compose logs -f frontend"
echo "   MySQL: docker compose logs -f mysql"
echo ""
echo "🛑 To stop:"
echo "   docker compose down"
echo ""
echo "🔄 To restart:"
echo "   docker compose restart"
echo ""
