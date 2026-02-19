@echo off
title Monetics - Arranque completo

echo ================================
echo   Iniciando Monetics...
echo ================================
echo.

:: Backend + MySQL con Docker
echo [1/2] Arrancando MySQL + Backend (Docker)...
cd /d %~dp0
docker compose up -d --build

:: Esperar a que el backend arranque (403 = Spring Security activo = backend listo)
echo Esperando al backend...
:wait_backend
timeout /t 5 /nobreak >nul
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/auth/login 2>nul | findstr /r "40[0-9] 200" >nul
if errorlevel 1 (
    echo   Aun arrancando...
    goto wait_backend
)
echo   Backend listo!

:: Frontend
echo [2/2] Arrancando Frontend (Angular)...
start "Monetics-Frontend" cmd /k "cd /d %~dp0MoneticsFront\monetics-app && npx ng serve --open"

echo.
echo ================================
echo   MySQL:    localhost:3306
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:4200
echo ================================
echo.
echo Para parar el backend: docker compose down
echo Puedes cerrar esta ventana.
pause
