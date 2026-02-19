# Estructura del Proyecto Monetics Frontend

## Descripción de carpetas

```
src/app/
├── core/                    # Funcionalidad principal de la aplicación
│   ├── services/           # Servicios singleton (API, Auth, etc.)
│   ├── guards/             # Guards de navegación
│   ├── interceptors/       # Interceptores HTTP
│   └── models/             # Modelos e interfaces de datos
│
├── shared/                  # Recursos compartidos entre módulos
│   ├── components/         # Componentes reutilizables
│   ├── directives/         # Directivas personalizadas
│   └── pipes/              # Pipes personalizados
│
├── features/                # Módulos de características
│   ├── auth/               # Autenticación y autorización
│   │   ├── login/
│   │   └── register/
│   └── dashboard/          # Panel principal
│
└── layouts/                 # Layouts de la aplicación
    └── main-layout/        # Layout principal

```

## Convenciones

### Servicios (core/services/)
- Servicios singleton que se usan en toda la aplicación
- Ejemplos: ApiService, AuthService, LocalStorageService

### Guards (core/guards/)
- Protección de rutas
- Ejemplos: AuthGuard, RoleGuard

### Interceptors (core/interceptors/)
- Interceptar y modificar peticiones HTTP
- Ejemplos: AuthInterceptor, ErrorInterceptor

### Models (core/models/)
- Interfaces y tipos TypeScript
- Clases de modelo de datos

### Shared Components
- Componentes reutilizables en toda la aplicación
- Ejemplos: Button, Modal, Card, Table

### Features
- Módulos de características específicas de la aplicación
- Cada feature tiene sus propios componentes, servicios y rutas

### Layouts
- Estructuras de página reutilizables
- Define la estructura general de las vistas

## Configuración

### Angular Material
- Ya configurado con tema `indigo-pink`
- Importar módulos según necesidad en cada componente

### Server-Side Rendering (SSR)
- Configurado y listo para usar
- Mejora el SEO y tiempo de carga inicial

### Routing
- Configuración principal en `app.routes.ts`
- Rutas lazy-loaded para optimizar el bundle size

## Proxy Backend
Configurado para conectar con el backend Spring Boot en `proxy.conf.json`
