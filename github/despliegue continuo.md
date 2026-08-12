# Estado del proyecto — API Plataforma de Seguros

**Fecha de este corte:** agosto 2026
**Repositorio:** https://github.com/samirmartinez1984/API-Plataforma-de-seguros-

---

## ✅ Lo que ya está completo (Fase 1)

### 1. Git y GitHub
- Repositorio inicializado y conectado a GitHub (rama `main`)
- Credenciales sensibles (contraseña de MySQL, JWT secret) externalizadas a variables de entorno con valores por defecto para desarrollo local:
  ```properties
  spring.datasource.username=${DB_USERNAME:root}
  spring.datasource.password=${DB_PASSWORD:admin}
  app.jwt.secret=${JWT_SECRET:...}
  ```
- README completo y actualizado (arquitectura, roles, stack, cómo correr el proyecto, roadmap)

### 2. Docker
- **Dockerfile** con estrategia multi-stage build:
  - Etapa 1 (`build`): imagen `maven:3.9-eclipse-temurin-21`, compila el `.jar`
  - Etapa 2 (`runtime`): imagen `eclipse-temurin:21-jre-alpine`, copia solo el `.jar` (imagen final ~131MB)
- **docker-compose.yml**: levanta la app y MySQL juntos en la misma red interna de Docker
  - MySQL expuesto en el puerto **3307** del host (no 3306, para evitar conflicto con MySQL local instalado en Windows)
  - La app se conecta a MySQL usando el nombre del servicio (`mysql:3306`), no `localhost`
  - Healthcheck en MySQL para evitar que la app arranque antes de que la BD esté lista
  - Volumen `mysql-data` para persistencia de datos
- Verificado localmente: `docker compose up -d` levanta ambos contenedores correctamente, Flyway corre las 3 migraciones, Swagger responde en `http://localhost:8080/api/swagger-ui.html`

### 3. CI/CD (GitHub Actions)
Archivo: `.github/workflows/ci-cd.yml`

Dos jobs configurados y **pasando en verde**:
- **`test`**: corre los 48 tests con H2 en memoria (perfil `test`)
- **`build-docker`**: construye la imagen Docker (solo si `test` pasa, vía `needs: test`)

**Problemas resueltos durante la construcción del pipeline** (documentados por si se repiten en otro proyecto):
1. `mvnw` sin permisos de ejecución en el runner de GitHub Actions (Linux) → se agregó `chmod +x mvnw` como paso del workflow, y también dentro del `Dockerfile` (dos lugares distintos donde se usa `mvnw`)
2. Test `InsurancePlatformSpringbootApplicationTests.contextLoads` fallaba en CI porque no tenía `@ActiveProfiles("test")` → intentaba conectarse a MySQL real en vez de usar H2. Se corrigió agregando la anotación.
3. Conflicto de puerto 3306 entre MySQL local de Windows y el contenedor de MySQL → resuelto exponiendo el contenedor en el puerto 3307 del host.
4. Bloqueos de archivos en `target/` causados por IntelliJ corriendo en segundo plano — se resolvió cerrando el IDE y usando terminal externa.

---

## ⏸️ Pendiente (pausado por decisión propia): Despliegue automático (CD)

### Qué se intentó
Se evaluó Railway como plataforma de despliegue (ya usada en otro proyecto anterior con éxito).

### Por qué se pausó
Railway **ya no ofrece un nivel gratuito real** en 2026:
- Elimina su free tier original en 2023
- Actualmente: trial único de $5 en créditos, luego mínimo $1-5/mes
- Ese mínimo no alcanza para correr la API + MySQL simultáneamente

### Alternativas investigadas (ninguna es un "gratis perfecto" para este stack exacto)

| Plataforma | Docker (app) | MySQL gratis | Limitación clave |
|---|---|---|---|
| **Railway** | Sí | Sí (nativo) | Ya no es gratis — $1-5/mes mínimo tras el trial |
| **Render** | Sí, gratis | ❌ Solo PostgreSQL gratis | Requeriría migrar de MySQL a PostgreSQL |
| **Fly.io** | Trial muy corto | — | Ya no da tier gratis a cuentas nuevas (2h VM o 7 días) |
| **InfinityFree** | ❌ No soporta contenedores/Java | Sí | Hosting compartido tipo cPanel, incompatible con Spring Boot |

### Opciones que quedan sobre la mesa para cuando se retome
1. **Pagar Railway** (~$5/mes) — la más simple y confiable, mismo flujo que ya se probó en el proyecto anterior
2. **Migrar de MySQL a PostgreSQL** y usar Render 100% gratis — implica cambios en `pom.xml` (driver), `application.properties`, y revisar sintaxis SQL específica de MySQL en las migraciones de Flyway si la hay
3. **Buscar otra alternativa gratuita** (ej. Clever Cloud, que en algunas búsquedas aparece con MySQL gratis — no verificado en profundidad todavía)

---

## 🔜 Próximos pasos para completar esta tarea (cuando se retome)

1. **Decidir** cuál de las 3 opciones de arriba se va a tomar
2. Si se elige pagar Railway o usar otra plataforma con soporte nativo de Docker + MySQL:
   - Crear el proyecto en la plataforma elegida, conectado al repo de GitHub
   - Generar el token/credencial de despliegue (ej. `RAILWAY_TOKEN`)
   - Guardarlo como **GitHub Secret** en el repositorio (`Settings → Secrets and variables → Actions`)
   - Agregar un tercer job `deploy` al archivo `.github/workflows/ci-cd.yml`, con `needs: [test, build-docker]`, para que el despliegue sea automático **solo si** los pasos anteriores pasan (esto es lo que técnicamente se llama *Continuous Deployment*, la preferencia ya confirmada sobre *Continuous Delivery* con aprobación manual)
   - Configurar las variables de entorno de producción (`DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` — este último debe ser un valor **nuevo**, distinto al que quedó como default en el código, ya que ese quedó expuesto en el repo público)
3. Si se elige migrar a PostgreSQL:
   - Cambiar dependencia del driver en `pom.xml` (de `mysql-connector-j` a `postgresql`)
   - Ajustar `spring.datasource.url` y el dialecto de Hibernate
   - Revisar los scripts de Flyway (`V1__Init.sql`, `V2__InsertRoles.sql`, `V3__AddPolicyNewFields.sql`) por sintaxis específica de MySQL que no sea compatible con PostgreSQL
   - Probar todo localmente con `docker-compose` antes de subir
4. Una vez desplegado, probar el flujo completo: hacer un cambio pequeño, push a `main`, confirmar que el pipeline corre los 3 jobs y la app se actualiza sola en producción

---

## 📌 Nota sobre lo que sigue en paralelo

Se acordó continuar con el **Grupo 1 de la Fase 2** (mejoras de arquitectura/DevOps) mientras se resuelve el tema del despliegue:
1. Spring Boot Actuator
2. Configuración externalizada (ya adelantada parcialmente en Fase 1)
3. Docker (ya completado)

Y después, el roadmap completo de Fase 2 incluye (en orden sugerido):
- Sistema de notificaciones (Spring Mail + `@Async`)
- Gestión de documentos (subida de archivos a reclamos)
- Dashboard y reportes
- Caché (Spring Cache + Caffeine/Redis)
- Auditoría de entidades (Spring Data Envers)
- Integración de pagos (Stripe/PayPal/Mercado Pago)