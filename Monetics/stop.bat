@echo off
title Monetics - Parar servicios

echo ================================
echo   Parando Monetics...
echo ================================
echo.

cd /d %~dp0

echo [1/2] Parando Backend + MySQL (Docker)...
docker compose down

echo [2/2] Parando Frontend (Angular)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :4200 ^| findstr LISTENING') do taskkill /F /PID %%a >nul 2>&1

echo.
echo ================================
echo   Todos los servicios parados.
echo ================================
pause
