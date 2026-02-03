-- Script para actualizar contraseñas a BCrypt y corregir roles
-- Todas las contraseñas serán: "password123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

USE monetics;

-- Admin
UPDATE usuarios SET
    password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    rol = 'ADMIN'
WHERE id_usuario = 1;

-- Manager
UPDATE usuarios SET
    password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    rol = 'MANAGER'
WHERE id_usuario = 2;

-- Empleados
UPDATE usuarios SET
    password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    rol = 'EMPLEADO'
WHERE id_usuario IN (3, 4, 5);

-- Verificar cambios
SELECT id_usuario, nombre, email, rol,
    CASE
        WHEN password LIKE '$2a$%' THEN 'BCrypt Hash ✓'
        ELSE 'Texto plano ✗'
    END as estado_password
FROM usuarios;
