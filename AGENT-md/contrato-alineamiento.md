 ARCHITECTURE_AGENT.md – insurance-platform-springboot
Contrato Maestro de Desarrollo (Monolito Multicapa)

Este documento es la **única fuente de verdad**. El agente (Copilot, desarrolladores, revisores) debe leer este archivo antes de generar cualquier código para asegurar la consistencia del proyecto.

---

 SKILL 1: CAPA DE MODELO (JPA & Persistence)

### Estándares de Implementación
- **Prohibido:** usar `@Data` en entidades.
- **Obligatorio:** usar `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- **Identidad:** implementar `equals()` y `hashCode()` solo con `id` (`@EqualsAndHashCode(onlyExplicitlyIncluded = true)`).
- **ToString:** usar `@ToString`, excluyendo relaciones (`@ToString.Exclude`).
- **Auditoría:** todas las tablas deben tener `created_at` (`LocalDateTime`).
- **Regla:** `created_at` se asigna en backend, nunca desde DTO de entrada.

### Diccionario de Datos Obligatorio
Todas las entidades deben seguir nombres y tipos estrictos:

**Role (`roles`)**
- `id`: Long (PK)
- `name`: String (Unique, Not Null, ej. ROLE_ADMIN)
- `description`: String
- `created_at`: LocalDateTime

**User (`users`)**
- `id`: Long (PK)
- `name`: String
- `username`: String (Unique, opcional)
- `email`: String (Unique, Not Null)
- `password_hash`: String (Not Null)
- `registered_at`: LocalDateTime
- `role_id`: Long (FK → roles.id)
- `enabled`: Boolean
- `created_at`: LocalDateTime

**CatalogProduct (`catalog_products`)**
- `id`: Long (PK)
- `name`: String
- `description`: String
- `coverage`: String/JSON
- `exclusions`: String/JSON
- `base_price`: BigDecimal (10,2)
- `type`: String (AUTO, LIFE, HEALTH, PETS, HOME)
- `status`: String (ACTIVE/INACTIVE)
- `created_at`: LocalDateTime

**Policy (`policies`)**
- `id`: Long (PK)
- `customer_id`: Long (FK → users.id)
- `product_id`: Long (FK → catalog_products.id)
- `start_date`: LocalDate
- `end_date`: LocalDate
- `status`: String (ACTIVE, EXPIRED, CANCELLED)
- `final_price`: BigDecimal (10,2)
- `created_at`: LocalDateTime

**Claim (`claims`)**
- `id`: Long (PK)
- `policy_id`: Long (FK → policies.id)
- `customer_id`: Long (FK → users.id)
- `supervisor_id`: Long (FK → users.id)
- `partner_id`: Long (FK → partners.id)
- `description`: String
- `reported_at`: LocalDateTime
- `status`: String (REGISTERED, IN_REVIEW, APPROVED, REJECTED)
- `created_at`: LocalDateTime

**Partner (`partners`)**
- `id`: Long (PK)
- `name`: String
- `type`: String (WORKSHOP, CLINIC, LABORATORY, VETERINARY)
- `address`: String
- `phone`: String
- `email`: String
- `status`: String (ACTIVE/INACTIVE)
- `created_at`: LocalDateTime

---

## SKILL 2: PERSISTENCIA Y MIGRACIONES
- Flyway obligatorio: scripts `.sql` en `src/main/resources/db/migration`.
- Tablas en plural y minúsculas, columnas en `snake_case`.
- Estrategia: `spring.jpa.hibernate.ddl-auto=validate`.
- No modificar tablas manualmente fuera de Flyway.

---

## SKILL 3: SEGURIDAD (JWT)
- Algoritmo: HS512.
- Header: `Authorization: Bearer <token>`.
- Roles: `ROLE_ADMIN` para gestión, `ROLE_USER` para consultas propias.
- Validación en Controller/Service, nunca en Repository/Mapper.

---

## SKILL 4: CAPA REPOSITORY
- Repositorios en `com.insurance.platform.repository`.
- Extender `JpaRepository<Entity, Long>`.
- Usar métodos derivados (`findBy...`, `existsBy...`) como primera opción.
- `@Query` solo si es necesario.
- No lógica de negocio en repositorios.

---

## SKILL 5: CAPA MAPPER
- Responsabilidad única: convertir **Entidad ↔ DTO**.
- Métodos estándar: `toDto`, `toEntity`, `updateEntityFromDto`.
- No mapear `created_at` desde cliente.
- DTO de salida sí expone `created_at`.

---

## SKILL 6: EXCEPCIONES Y MANEJO GLOBAL
- Paquete: `com.insurance.platform.exception`.
- Excepciones mínimas:
  - `RecursoNoEncontradoException` → 404
  - `DatosInvalidosException` → 400
  - `ConflictoException` → 409
  - `NoAutorizadoException` → 401
- `GlobalExceptionHandler` con `@RestControllerAdvice`.
- Contrato uniforme `ApiError` con: `status`, `error`, `message`, `path`, `details`, `timestamp`.

---

## SKILL 7: CAPA SERVICE
- Lógica de negocio y validaciones.
- Anotar con `@Service`.
- `@Transactional(readOnly = true)` en consultas.
- `@Transactional` en escritura.
- Asignar campos de sistema en backend (`created_at`, cálculos de prima, etc.).

---

## SKILL 8: CAPA CONTROLLER (API REST)
- Endpoints REST en `com.insurance.platform.controller`.
- Usar DTOs en entrada/salida.
- Validaciones con `@Valid`.
- Responder con `ResponseEntity`.
- Convención de endpoints:
  - `POST /api/{resource}` → crear
  - `GET /api/{resource}` → listar
  - `GET /api/{resource}/{id}` → obtener por id
  - `PUT /api/{resource}/{id}` → actualizar
  - `DELETE /api/{resource}/{id}` → eliminar

---

## SKILL 9: FLUJO DE CAPAS OBLIGATORIO
- Flujo estándar: `Controller → Service → Repository`.
- Mapper en frontera de datos.
- Excepciones nacen en Service y se manejan en `GlobalExceptionHandler`.
- Prohibido saltar capas.
