# Cómo hacer login en la aplicación Monetics

## 🚨 Importante: Antes de intentar hacer login

Tu base de datos tiene contraseñas en **texto plano**, pero Spring Security espera contraseñas **hasheadas con BCrypt**.

## Paso 1: Actualizar la base de datos

Ejecuta el script SQL que se encuentra en la raíz del proyecto:

```bash
cd c:\Users\Beatriz\Documents\MoneticsProyect\Monetics\Monetics
```

Ejecuta el archivo `update_passwords.sql` en tu base de datos MySQL:

```bash
mysql -u root -p monetics < update_passwords.sql
```

O ábrelo en phpMyAdmin y ejecútalo.

Este script hará dos cosas:
1. ✅ Convertir las contraseñas de texto plano a BCrypt
2. ✅ Corregir los nombres de los roles (ROLE_USER → EMPLEADO, etc.)

## Paso 2: Verificar que el backend está corriendo

1. Inicia tu aplicación Spring Boot:
   ```bash
   cd Monetics/MoneticsBack/MoneticsBack
   mvn spring-boot:run
   ```

2. Verifica que está corriendo en `http://localhost:8080`

**IMPORTANTE**: Asegúrate de haber implementado los endpoints de autenticación siguiendo la guía en [BACKEND-TODO.md](BACKEND-TODO.md).

## Paso 3: Iniciar el frontend

```bash
cd Monetics/MoneticsFront/monetics-app
npm start
```

La aplicación estará disponible en `http://localhost:4200`

## Paso 4: Hacer login

### 👤 Usuarios disponibles

#### **Manager** (Laura)
Puede ver todos los gastos de su equipo:
- **Email**: `laura.manager@monetics.com`
- **Password**: `password123`
- **Rol**: MANAGER
- Puede ver: Gastos de Carlos y Ana (sus empleados)

#### **Empleado** (Carlos)
Puede crear y ver sus propios gastos:
- **Email**: `carlos.dev@monetics.com`
- **Password**: `password123`
- **Rol**: EMPLEADO
- Manager: Laura

#### **Empleado** (Ana)
Puede crear y ver sus propios gastos:
- **Email**: `ana.dev@monetics.com`
- **Password**: `password123`
- **Rol**: EMPLEADO
- Manager: Laura

#### **Admin**
Acceso administrativo:
- **Email**: `admin@monetics.com`
- **Password**: `password123`
- **Rol**: ADMIN

---

## 🎯 Flujo de prueba recomendado

### 1. Prueba como Empleado (Carlos)
1. Login con `carlos.dev@monetics.com` / `password123`
2. Verás tus gastos personales (debería haber algunos en la tabla)
3. Haz clic en "Nuevo Gasto"
4. Rellena el formulario y guarda
5. El nuevo gasto aparecerá en la tabla

### 2. Prueba como Manager (Laura)
1. Logout (menú superior derecho)
2. Login con `laura.manager@monetics.com` / `password123`
3. Verás TODOS los gastos de tu equipo (Carlos y Ana)
4. No podrás crear gastos (solo los empleados pueden)

---

## ❌ Si no puedes hacer login

### Error: "Error al iniciar sesión. Verifica tus credenciales"

**Causa**: Los endpoints de autenticación no están implementados en el backend

**Solución**:
1. Revisa [BACKEND-TODO.md](BACKEND-TODO.md)
2. Implementa `AuthController` con los endpoints `/api/auth/login` y `/api/auth/register`
3. Asegúrate de que `JwtUtil` y `JwtAuthenticationFilter` estén configurados

### Error de CORS

**Causa**: El backend no permite peticiones desde `http://localhost:4200`

**Solución**: Añade configuración CORS en tu backend (ver [BACKEND-TODO.md](BACKEND-TODO.md))

### Error 404: Cannot POST /api/auth/login

**Causa**: El `AuthController` no existe o no está mapeado correctamente

**Solución**: Verifica que `AuthController.java` tenga la anotación `@RestController` y `@RequestMapping("/api/auth")`

---

## 🔍 Verificar que todo funciona

### Prueba manual del backend

Usa Postman o curl para probar el endpoint de login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"carlos.dev@monetics.com","password":"password123"}'
```

Deberías recibir:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "idUsuario": 3,
    "nombre": "Carlos Developer",
    "email": "carlos.dev@monetics.com",
    "rol": "EMPLEADO"
  }
}
```

---

## 📱 ¿Qué verás después del login?

### Vista Empleado:
- Barra superior con tu nombre y rol
- Botón "Nuevo Gasto"
- Tabla con tus gastos:
  - Descripción
  - Fecha
  - Importe original (con moneda)
  - Importe en EUR
  - Estado (Pendiente/Aprobado/Rechazado)

### Vista Manager:
- Barra superior con tu nombre y rol
- Título: "Gastos del Equipo"
- Tabla con TODOS los gastos de tus empleados
- NO hay botón "Nuevo Gasto" (solo empleados pueden crear)

---

## 🆘 Ayuda adicional

Si sigues teniendo problemas:

1. Abre la consola del navegador (F12) y mira los errores
2. Verifica los logs del backend Spring Boot
3. Asegúrate de que la base de datos está corriendo
4. Verifica que ejecutaste el script `update_passwords.sql`

---

## 🎨 Personalización

Una vez que puedas hacer login y todo funcione, puedes personalizar:

- **Colores**: Cambia el gradiente en `login.component.css` y `register.component.css`
- **Logo**: Añade tu logo en la barra superior del dashboard
- **Tema Material**: Cambia el tema en `angular.json` (actualmente: indigo-pink)
- **Estilos de tabla**: Modifica `dashboard.component.css`

¡Disfruta de tu aplicación Monetics! 🚀
