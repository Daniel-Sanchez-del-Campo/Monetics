# Monetics Frontend

Aplicación frontend del proyecto Monetics desarrollada con Angular 18.

## Características

- Angular 18 con Standalone Components
- Angular Material para componentes UI
- Server-Side Rendering (SSR)
- Routing configurado
- Proxy configurado para backend Spring Boot
- Interceptor de autenticación JWT
- Estructura de carpetas organizada

## Requisitos previos

- Node.js 18 o superior
- npm o yarn

## Instalación

```bash
# Instalar dependencias
npm install
```

## Scripts disponibles

```bash
# Desarrollo con proxy (conecta al backend en localhost:8080)
npm start

# Desarrollo sin proxy
npm run start:no-proxy

# Build de producción
npm run build

# Ejecutar tests
npm test

# Build en modo watch
npm run watch

# Servir aplicación con SSR
npm run serve:ssr:monetics-app
```

## Estructura del proyecto

```
src/app/
├── core/                    # Funcionalidad principal
│   ├── services/           # Servicios (API, Auth, etc.)
│   ├── guards/             # Guards de navegación
│   ├── interceptors/       # Interceptores HTTP
│   └── models/             # Modelos e interfaces
│
├── shared/                  # Recursos compartidos
│   ├── components/         # Componentes reutilizables
│   ├── directives/         # Directivas personalizadas
│   └── pipes/              # Pipes personalizados
│
├── features/                # Módulos de características
│   ├── auth/               # Autenticación
│   └── dashboard/          # Dashboard
│
└── layouts/                 # Layouts de la aplicación
    └── main-layout/        # Layout principal
```

Ver [README-ESTRUCTURA.md](README-ESTRUCTURA.md) para más detalles sobre la organización del proyecto.

## Configuración del Backend

El proxy está configurado para redirigir todas las peticiones que comienzan con `/api` al backend en `http://localhost:8080`.

Para cambiar la URL del backend:

1. **Desarrollo**: Edita [proxy.conf.json](proxy.conf.json)
2. **Producción**: Edita [src/environments/environment.prod.ts](src/environments/environment.prod.ts)

## Autenticación

El proyecto incluye un interceptor de autenticación en [src/app/core/interceptors/auth.interceptor.ts](src/app/core/interceptors/auth.interceptor.ts) que automáticamente añade el token JWT a todas las peticiones HTTP.

El token se obtiene del `localStorage` con la clave `token`.

## Servicios

### ApiService

Servicio base para realizar peticiones HTTP. Ubicado en [src/app/core/services/api.service.ts](src/app/core/services/api.service.ts).

Ejemplo de uso:

```typescript
import { ApiService } from './core/services/api.service';

export class MiComponente {
  constructor(private apiService: ApiService) {}

  obtenerDatos() {
    this.apiService.get('/usuarios').subscribe(data => {
      console.log(data);
    });
  }
}
```

## Angular Material

Angular Material está instalado con el tema `indigo-pink`. Para usar componentes de Material:

1. Importa el módulo del componente en tu componente standalone
2. Usa el componente en tu template

Ejemplo:

```typescript
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-mi-componente',
  standalone: true,
  imports: [MatButtonModule],
  template: '<button mat-raised-button>Click me</button>'
})
export class MiComponente {}
```

## Server-Side Rendering (SSR)

El proyecto está configurado con SSR para mejorar el SEO y el tiempo de carga inicial.

Para construir y servir con SSR:

```bash
npm run build
npm run serve:ssr:monetics-app
```

## Desarrollo

1. Asegúrate de que el backend Spring Boot esté corriendo en `http://localhost:8080`
2. Ejecuta `npm start` para iniciar el servidor de desarrollo
3. Navega a `http://localhost:4200`

La aplicación se recargará automáticamente si cambias alguno de los archivos fuente.

## Build

Ejecuta `npm run build` para construir el proyecto. Los artefactos de construcción se almacenarán en el directorio `dist/`.

## Próximos pasos

1. Implementar componentes de autenticación (login, registro)
2. Crear servicios específicos del dominio
3. Desarrollar las features principales
4. Añadir tests unitarios y e2e
5. Configurar CI/CD

## Recursos

- [Documentación de Angular](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [Angular SSR](https://angular.io/guide/ssr)
