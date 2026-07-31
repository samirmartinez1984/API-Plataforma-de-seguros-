# API Plataforma de Seguros

API REST para la gestión integral del ciclo de vida de seguros: catálogo de productos, pólizas, reclamos (siniestros) y socios (partners), con autenticación basada en JWT y control de acceso por roles.

## Estado del proyecto

- ✅ **Fase 1 — Completada**: API funcional, autenticación JWT, CRUD completo, tests unitarios e integración, migraciones con Flyway.
- 🚧 **Fase 2 — En desarrollo**: Docker, CI/CD, notificaciones, gestión de documentos, caché, auditoría, dashboard y más.

## Arquitectura

El proyecto sigue el patrón de tres capas, estándar en aplicaciones Spring Boot:

```
Controller  →  Service  →  Repository  →  Base de datos (MySQL)
```

- **Controller**: expone los endpoints REST y valida las peticiones de entrada.
- **Service**: contiene la lógica de negocio.
- **Repository**: acceso a datos vía Spring Data JPA.
- **DTOs + MapStruct**: se usan DTOs (request/response/update) para no exponer las entidades JPA directamente, con mapeo automático vía MapStruct.

## Roles y funcionalidades

| Rol | Permisos |
|---|---|
| **ADMIN** | Gestión total: usuarios, roles, catálogo de productos, pólizas, reclamos y socios. |
| **USER** (cliente) | Consulta el catálogo, adquiere pólizas, registra reclamos sobre sus pólizas activas, consulta sus propias pólizas/reclamos. |
| **SUPERVISOR** | Gestiona los reclamos reportados por clientes: actualiza estados y asigna socios (talleres, clínicas, etc.). |

**Flujo principal:**
1. Un **ADMIN** define los productos de seguro en el catálogo.
2. Un **cliente** se registra y adquiere una póliza basada en un producto.
3. Ante un incidente, el cliente crea un **reclamo** asociado a su póliza.
4. Un **SUPERVISOR** revisa el reclamo y lo asigna a un **partner** para su resolución.

## Stack tecnológico

- **Framework**: Spring Boot
- **Lenguaje**: Java 21
- **Base de datos**: MySQL (H2 en memoria para tests)
- **Persistencia**: Spring Data JPA + Hibernate
- **Migraciones**: Flyway
- **Seguridad**: Spring Security + JWT
- **Documentación API**: SpringDoc (OpenAPI/Swagger)
- **Mapeo de objetos**: MapStruct
- **Build**: Maven
- **Utilidades**: Lombok
- **Tests**: JUnit 5, Mockito, AssertJ

## Requisitos previos

- Java 21
- Maven (o usar el wrapper incluido `./mvnw`)
- MySQL corriendo localmente (o accesible por red)

## Cómo ejecutar el proyecto localmente

### 1. Clonar el repositorio

```bash
git clone https://github.com/samirmartinez1984/API-Plataforma-de-seguros-.git
cd API-Plataforma-de-seguros-
```

### 2. Configurar variables de entorno (opcional)

El proyecto trae valores por defecto para desarrollo local, pero puedes sobreescribirlos con variables de entorno:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `admin` |
| `JWT_SECRET` | Clave para firmar tokens JWT | valor de desarrollo incluido |

> ⚠️ En un entorno de producción, `JWT_SECRET` **siempre** debe definirse como variable de entorno propia, nunca usar el valor por defecto del repositorio.

### 3. Crear la base de datos

Crea una base de datos MySQL llamada `plataforma_seguros`. Las tablas se generan automáticamente al arrancar la app gracias a Flyway (ver `src/main/resources/db/migration/`).

### 4. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La API quedará disponible en:

```
http://localhost:8080/api
```

### 5. Documentación interactiva (Swagger)

- UI: `http://localhost:8080/api/swagger-ui.html`
- JSON OpenAPI: `http://localhost:8080/api/v3/api-docs`

## Ejecutar tests

```bash
./mvnw test
```

Los tests de integración usan una base de datos H2 en memoria (`src/test/resources/application-test.properties`), por lo que no requieren MySQL corriendo.

## Generar el .jar ejecutable

```bash
./mvnw clean package
```

El artefacto se genera en `target/*.jar` y puede ejecutarse con:

```bash
java -jar target/insurance-platform-springboot-0.0.1-SNAPSHOT.jar
```

## Migraciones de base de datos

El proyecto usa Flyway. Los scripts viven en `src/main/resources/db/migration/` y se ejecutan automáticamente al arrancar la aplicación:

- `V1__Init.sql`: creación de tablas principales (`roles`, `users`, `catalog_products`, `policies`, `partners`, `claims`).
- `V2__InsertRoles.sql`: inserción de roles base (ADMIN, USER, SUPERVISOR).
- `V3__AddPolicyNewFields.sql`: campos adicionales en pólizas.

## Roadmap (Fase 2)

- [ ] Contenerización con Docker + docker-compose
- [ ] Pipeline CI/CD (GitHub Actions)
- [ ] Spring Boot Actuator (monitoreo)
- [ ] Sistema de notificaciones (email, async)
- [ ] Gestión de documentos adjuntos en reclamos
- [ ] Caché (Spring Cache + Caffeine/Redis)
- [ ] Auditoría de entidades (Spring Data Envers)
- [ ] Dashboard y reportes
- [ ] Integración de pagos

## Licencia

Proyecto educativo / portafolio personal.