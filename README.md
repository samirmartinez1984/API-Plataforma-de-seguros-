# Plataforma de Seguros - API REST

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

Una aplicación empresarial de **gestión integral de seguros** construida con Spring Boot 3.5, implementando patrones arquitectónicos modernos, seguridad robusta basada en JWT, y prácticas DevOps de clase mundial.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Variables de Entorno](#variables-de-entorno)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [API Endpoints](#api-endpoints)
- [Seguridad](#seguridad)
- [Monitoreo y Observabilidad](#monitoreo-y-observabilidad)
- [Testing](#testing)
- [Despliegue](#despliegue)
- [Roadmap](#roadmap)

---

## ✨ Características

### Funcionalidades Principales

- **Autenticación y Autorización**: Sistema de roles basado en JWT con tres perfiles (`ADMIN`, `USER`, `SUPERVISOR`)
- **Gestión de Pólizas**: Crear, actualizar y consultar pólizas de seguros con ciclo de vida completo
- **Sistema de Reclamos**: Registro, seguimiento y gestión de siniestros con estados predefinidos
- **Catálogo de Productos**: Administración centralizada de productos y planes de seguros
- **Gestión de Socios**: Control de partners asegurados (talleres, clínicas, etc.)
- **Notificaciones por Correo**: Sistema asíncrono de emails automáticos en eventos clave (registro, reclamos)
- **Gestión de Documentos**: Subida y almacenamiento de archivos adjuntos en reclamos con UUID únicos
- **Monitoreo en Tiempo Real**: Health checks y métricas vía Spring Boot Actuator
- **Documentación Interactiva**: API Explorer integrado con Swagger/OpenAPI

### Características Técnicas

- ✅ Arquitectura de 3 capas (Controller → Service → Repository)
- ✅ Validación de datos robusta en todas las capas
- ✅ Manejo centralizado de excepciones
- ✅ Migrations versionadas con Flyway (V1-V4)
- ✅ Logging estructurado
- ✅ Pipeline CI/CD automático (GitHub Actions)
- ✅ Containerización con Docker y docker-compose
- ✅ +48 pruebas unitarias e integración
- ✅ Operaciones asíncronas con @Async

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    Cliente (REST/HTTP)                  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│           Capa de Controladores (REST API)              │
│  AuthController │ UserController │ ClaimController ...  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│             Capa de Servicios (Lógica de Negocio)       │
│  UserService │ ClaimService │ NotificationService ...   │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│            Capa de Repositorios (Acceso a Datos)        │
│  UserRepository │ ClaimRepository │ DocumentRepository  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                   Base de Datos (MySQL)                 │
│              Persistencia y Transacciones                │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| **Lenguaje** | Java | 21 |
| **Framework** | Spring Boot | 3.5.14 |
| **ORM** | Hibernate/JPA | Via Spring Data |
| **Base de Datos** | MySQL | 8.0 |
| **Seguridad** | Spring Security + JWT | HS512 |
| **Migraciones** | Flyway | Auto-versionado |
| **Notificaciones** | Spring Mail | SMTP async |
| **Documentación** | SpringDoc OpenAPI | Swagger 3.0 |
| **Mapeo** | MapStruct | Code generation |
| **Testing** | JUnit 5 + Mockito | Integración completa |
| **DevOps** | Docker + GitHub Actions | CI/CD |
| **Build** | Maven | 3.8+ |

---

## 📋 Requisitos Previos

### Desarrollo Local
- Java 21+
- MySQL 8.0+
- Maven 3.8+
- Git
- Docker & Docker Compose (opcional)

### Herramientas Recomendadas
- IntelliJ IDEA o VS Code
- Postman/Insomnia
- MySQL Workbench

---

## 🚀 Instalación y Configuración

### 1. Clonar Repositorio

```bash
git clone https://github.com/samirmartinez1984/API-Plataforma-de-seguros-.git
cd API-Plataforma-de-seguros-
```

### 2. Configurar Variables de Entorno

```bash
# Windows (PowerShell)
[System.Environment]::SetEnvironmentVariable("DB_USERNAME", "root", "User")
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "admin", "User")
[System.Environment]::SetEnvironmentVariable("JWT_SECRET", "tu_clave_512bits_aqui", "User")
[System.Environment]::SetEnvironmentVariable("MAIL_USERNAME", "usuario@mailtrap.io", "User")
[System.Environment]::SetEnvironmentVariable("MAIL_PASSWORD", "tu_contraseña", "User")
```

### 3. Ejecutar Localmente

```bash
# Tests
./mvnw clean test

# Aplicación
./mvnw spring-boot:run
```

Acceso en: `http://localhost:8080/api`

### 4. Ejecutar con Docker

```bash
docker-compose up -d
```

---

## 🔐 Variables de Entorno

| Variable | Descripción | Requisito |
|----------|-------------|-----------|
| `DB_USERNAME` | Usuario MySQL | root |
| `DB_PASSWORD` | Contraseña MySQL | admin |
| `JWT_SECRET` | Clave JWT (≥512 bits) | Cryptográficamente seguro |
| `MAIL_USERNAME` | Usuario SMTP | Mailtrap/Gmail/Outlook |
| `MAIL_PASSWORD` | Contraseña SMTP | Token de aplicación |
| `app.upload.dir` | Directorio de archivos | uploads/claims (default) |

**⚠️ Nunca commitear credenciales reales.**

---

## 📡 API Endpoints Principales

### Autenticación
- `POST /auth/register` - Registrar usuario
- `POST /auth/login` - Login y obtener token

### Gestión de Datos
- `GET/POST/PUT /policies` - Pólizas
- `GET/POST/PUT /claims` - Reclamos
- `POST/GET /claims/{id}/documents` - Documentos

### Monitoreo
- `GET /actuator/health` - Estado de la app (público)
- `GET /actuator/info` - Información (público)
- `GET /actuator/metrics` - Métricas (autenticado)

### Documentación
- `GET /swagger-ui.html` - UI interactiva
- `GET /v3/api-docs` - Especificación OpenAPI

---

## 🔒 Seguridad

- ✅ JWT con HS512 (512+ bits)
- ✅ Contraseñas BCrypt
- ✅ RBAC (Role-Based Access Control)
- ✅ CORS restringido
- ✅ Validación de entrada
- ✅ Variables de entorno para secretos

---

## ✅ Testing

```bash
# Ejecutar todos los tests (48+)
./mvnw test

# Tests específicos
./mvnw test -Dtest=UserServiceTest
```

---

## 🚢 Despliegue

### Local
```bash
docker-compose up -d
```

### Pipeline CI/CD
El proyecto incluye GitHub Actions que:
1. Ejecuta tests en cada push
2. Construye imagen Docker
3. Genera reportes

Ver: [Actions](https://github.com/samirmartinez1984/API-Plataforma-de-seguros-/actions)

---

## 📋 Roadmap

**Fase 1** ✅ - Base técnica, auth, actuator, notificaciones, documentos
**Fase 2** 🔄 - Dashboard, reportes, caché
**Fase 3** 📋 - Pagos, microservicios, WebSockets

---

**Última actualización**: Agosto 2026 | **Versión**: 2.0.0