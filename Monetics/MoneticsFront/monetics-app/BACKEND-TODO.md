# Endpoints del Backend que necesitas implementar

El frontend está completamente funcional, pero requiere que implementes los siguientes endpoints en tu backend Spring Boot:

## Endpoints de Autenticación (FALTA IMPLEMENTAR)

### 1. POST /api/auth/login
**Descripción**: Endpoint para iniciar sesión

**Request Body**:
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña123"
}
```

**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "email": "usuario@ejemplo.com",
    "rol": "EMPLEADO"
  }
}
```

**Errores**:
- 401 Unauthorized: Credenciales inválidas
- 400 Bad Request: Datos faltantes o inválidos

---

### 2. POST /api/auth/register
**Descripción**: Endpoint para registrar un nuevo usuario

**Request Body**:
```json
{
  "nombre": "Juan Pérez",
  "email": "usuario@ejemplo.com",
  "password": "contraseña123",
  "idDepartamento": 1
}
```

**Response (201 Created)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "email": "usuario@ejemplo.com",
    "rol": "EMPLEADO"
  }
}
```

**Errores**:
- 409 Conflict: Email ya existe
- 400 Bad Request: Datos inválidos

---

## Notas Importantes

### Security Configuration
Tu `SecurityConfig.java` ya tiene configurado:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .anyRequest().authenticated()
);
```

Esto significa que `/api/auth/login` y `/api/auth/register` deberían ser públicos.

### Ejemplo de implementación de AuthController

Crea un archivo `AuthController.java` en `com.monetics.moneticsback.controller`:

```java
package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.LoginRequest;
import com.monetics.moneticsback.dto.LoginResponse;
import com.monetics.moneticsback.dto.RegisterRequest;
import com.monetics.moneticsback.security.JwtUtil;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioService usuarioService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Autenticar usuario
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Generar token JWT
        String token = jwtUtil.generateToken(request.getEmail());

        // Obtener datos del usuario
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorEmail(request.getEmail());

        // Crear respuesta
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsuario(usuario);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        // Crear usuario
        UsuarioDTO usuario = usuarioService.crearUsuario(request);

        // Generar token
        String token = jwtUtil.generateToken(usuario.getEmail());

        // Crear respuesta
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsuario(usuario);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
```

### DTOs que necesitas crear

**LoginRequest.java**:
```java
package com.monetics.moneticsback.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Getters y setters
}
```

**RegisterRequest.java**:
```java
package com.monetics.moneticsback.dto;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Long idDepartamento;

    // Getters y setters
}
```

**LoginResponse.java**:
```java
package com.monetics.moneticsback.dto;

public class LoginResponse {
    private String token;
    private UsuarioDTO usuario;

    // Getters y setters
}
```

### Filtro JWT (IMPORTANTE)

También necesitas crear un filtro JWT que intercepte las peticiones y valide el token. Este filtro debe:

1. Extraer el token del header `Authorization: Bearer <token>`
2. Validar el token usando `JwtUtil`
3. Si es válido, autenticar al usuario en el contexto de Spring Security

Ejemplo básico de `JwtAuthenticationFilter.java`:

```java
package com.monetics.moneticsback.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

Y luego agregarlo en `SecurityConfig.java`:

```java
@Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

## Endpoints Ya Implementados ✅

Los siguientes endpoints ya existen en tu backend y están funcionando:

- ✅ GET /api/usuarios/{idUsuario}
- ✅ GET /api/usuarios/manager/{idManager}/empleados
- ✅ GET /api/gastos/usuario/{idUsuario}
- ✅ GET /api/gastos/manager/{idManager}
- ✅ POST /api/gastos/usuario/{idUsuario}

---

## Pruebas

### Datos de prueba recomendados

Crea al menos dos usuarios en tu base de datos para probar:

1. **Empleado**:
   - Email: empleado@monetics.com
   - Password: password123
   - Rol: EMPLEADO

2. **Manager**:
   - Email: manager@monetics.com
   - Password: password123
   - Rol: MANAGER

### Secuencia de prueba

1. Inicia el backend Spring Boot en puerto 8080
2. Inicia el frontend Angular: `cd monetics-app && npm start`
3. Abre `http://localhost:4200`
4. Intenta hacer login con las credenciales de prueba
5. Una vez autenticado, el dashboard debería cargar automáticamente
6. Prueba crear un nuevo gasto (solo para empleados)
7. Verifica que la tabla muestre los gastos

---

## CORS (Importante para desarrollo)

Asegúrate de configurar CORS en tu backend para permitir peticiones desde `http://localhost:4200`:

```java
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

## Resumen

Para que el frontend funcione completamente, necesitas:

1. ✅ Crear `AuthController` con endpoints `/login` y `/register`
2. ✅ Crear los DTOs: `LoginRequest`, `RegisterRequest`, `LoginResponse`
3. ✅ Implementar `JwtAuthenticationFilter`
4. ✅ Configurar el filtro en `SecurityConfig`
5. ✅ Configurar CORS
6. ✅ Crear usuarios de prueba en la base de datos

Una vez implementado todo esto, el frontend estará completamente funcional y podrás empezar a personalizarlo estéticamente.
