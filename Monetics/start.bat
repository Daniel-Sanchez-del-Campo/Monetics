@echo off
title Monetics - Arranque completo

echo ================================
echo   Iniciando Monetics...
echo ================================
echo.

:: Backend
echo [1/2] Arrancando Backend (Spring Boot)...
start "Monetics-Backend" cmd /k "cd /d %~dp0MoneticsBack\MoneticsBack && mvnw spring-boot:run"

:: Esperar a que el backend arranque
echo Esperando al backend...
timeout /t 20 /nobreak >nul

:: Frontend
echo [2/2] Arrancando Frontend (Angular)...
start "Monetics-Frontend" cmd /k "cd /d %~dp0MoneticsFront\monetics-app && npx ng serve --open"

echo.
echo ================================
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:4200
echo ================================
echo.
echo Puedes cerrar esta ventana.
pause
