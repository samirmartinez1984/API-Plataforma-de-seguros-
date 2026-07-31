# 📖 Documentación de Funcionamiento de los Tests de Integración

Este documento detalla el diseño, la configuración y el funcionamiento de toda la suite de **pruebas de integración** desarrolladas para el monolito de seguros (`insurance-platform-springboot`).

---

## ⚙️ Arquitectura y Configuración General

Para garantizar el aislamiento de datos y no alterar la base de datos de producción (MySQL), las pruebas de integración utilizan:
* **Base de datos H2 en memoria:** Creada dinámicamente al iniciar los tests.
* **Perfil activo de Spring:** `test` (carga la configuración desde [application-test.properties](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/resources/application-test.properties)).
* **Hibernate ddl-auto:** `create-drop` (genera la base de datos de manera limpia para cada clase de test y la elimina al finalizar).
* **Flyway desactivado:** `spring.flyway.enabled=false` para acelerar los tiempos de ejecución de las pruebas.

### 🏛️ Clase Base: `BaseRepositoryIntegracionTest`

Todas las clases de test de integración heredan de la clase abstracta [BaseRepositoryIntegracionTest.java](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/BaseRepositoryIntegracionTest.java). Esta clase se encarga de:
1. **Configurar el entorno:**
   * `@DataJpaTest`: Arranca un contexto mínimo de Spring con JPA y Repositorios.
   * `@ActiveProfiles("test")`: Activa el perfil de configuración en memoria.
   * `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)`: Desactiva el reemplazo automático por H2 embebido por defecto para forzar la configuración explícita definida en nuestro `application-test.properties`.
2. **Proveer Builders / Creadores comunes:**
   * Métodos auxiliares como `crearRole()`, `crearUser()`, `crearCatalogProduct()`, `crearPartner()`, `crearPolicy()`, y `crearClaim()`. Estos métodos aseguran que todas las entidades se construyan con **datos obligatorios válidos** (como el campo `createdAt` que causaba el error original de integridad).

---

## 🧪 Detalle de Clases de Test y Casos Evaluados

### 1. 🧩 [UserRepositoryIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/UserRepositoryIntegracionTest.java)
Valida el funcionamiento de la persistencia de usuarios y su relación con el rol de seguridad asignado.

* **`findByEmail_DebeRetornarUsuario_CuandoEmailExiste`**
  * *Acción:* Guarda un `Role` y un `User` asociado en H2. Luego, realiza una búsqueda por email.
  * *Verificación:* Asegura que el `Optional<User>` esté presente y que el correo y el nombre del rol recuperado coincidan con los guardados.
* **`existsByEmail_DebeRetornarTrue_CuandoEmailExiste`**
  * *Acción:* Verifica la utilidad del método `existsByEmail` buscando un correo existente e inexistente.
  * *Verificación:* Retorna `true` para el correo existente y `false` para el inexistente.
* **`existsByUsername_DebeRetornarTrue_CuandoUsernameExiste`**
  * *Acción:* Valida la unicidad del nombre de usuario (`existsByUsername`).
  * *Verificación:* Retorna `true` al consultar el username persistido y `false` para uno no registrado.

### 2. 🧩 [CatalogProductRepositoryIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/CatalogProductRepositoryIntegracionTest.java)
Prueba la persistencia de productos comerciales del catálogo y las búsquedas por tipos, estados y rangos de precio.

* **`saveAndFindById_DebePersistirProductoCorrectamente`**
  * *Acción:* Crea y persiste un producto del catálogo (ej. Auto).
  * *Verificación:* Valida que los datos de precio, tipo, estado y descripción se recuperen intactos.
* **`findByType_DebeRetornarProductosFiltrados`**
  * *Acción:* Persiste varios productos de distintos tipos (`AUTO`, `LIFE`, `HOME`) y busca por `ProductType.AUTO`.
  * *Verificación:* Retorna únicamente los productos que correspondan al tipo buscado.
* **`findByStatus_DebeRetornarProductosSegunEstado`**
  * *Acción:* Persiste productos activos e inactivos y realiza la búsqueda según su `ProductStatus`.
  * *Verificación:* Clasifica y devuelve de forma precisa los elementos correspondientes.
* **`findByBasePriceLessThan_DebeRetornarProductosBaratos`**
  * *Acción:* Guarda productos con precios altos y bajos, y busca los que son menores a `$100`.
  * *Verificación:* Compara numéricamente los precios a través del motor SQL.
* **`findByNameProductContainingIgnoreCase_DebeRetornarProductosPorPalabraClave`**
  * *Acción:* Evalúa búsquedas de texto parcial (ej. buscando "salud" en "Seguro de Salud Familiar"), ignorando mayúsculas.
  * *Verificación:* Retorna la lista que contiene la palabra clave.
* **`existsByNameProduct_DebeRetornarTrue_CuandoProductoExiste`**
  * *Acción:* Comprueba la validación de nombre duplicado en catálogo.

### 3. 🧩 [PartnerRepositoryIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/PartnerRepositoryIntegracionTest.java)
Prueba la correcta persistencia y búsqueda de proveedores o aliados externos.

* **`saveAndFindById_DebePersistirPartnerCorrectamente`**
  * *Acción:* Guarda y recupera un socio con su tipo y estado.
* **`findByType_DebeRetornarPartnersDelTipoIndicado`**
  * *Acción:* Filtra aliados por tipo (`CLINIC`, `VETERINARY`, etc.).
* **`findByStatus_DebeRetornarPartnersConEstadoIndicado`**
  * *Acción:* Filtra por estado (`ACTIVE`/`INACTIVE`).
* **`findByEmail_DebeRetornarPartner_CuandoEmailExiste`**
  * *Acción:* Busca de forma opcional por email único.
* **`findByPartnerNameContainingIgnoreCase_DebeRetornarCoincidencias`**
  * *Acción:* Busca socios usando coincidencia parcial del nombre sin distinguir mayúsculas.
* **`existsByPartnerName_DebeRetornarTrue_CuandoNombreExiste`**
  * *Acción:* Comprueba la unicidad del nombre registrado del proveedor.

### 4. 🧩 [PolicyRepositoryIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/PolicyRepositoryIntegracionTest.java)
Valida la generación de contratos de pólizas, ligando claves foráneas reales a clientes (`User`) y productos comerciales (`CatalogProduct`).

* **`saveAndFindById_DebePersistirPolizaCorrectamente`**
  * *Acción:* Persiste una póliza calculando un precio final con descuento.
  * *Verificación:* Asegura que el cliente y el producto asociados coincidan en H2.
* **`findByCustomerId_DebeRetornarPolizasDelCliente`**
  * *Acción:* Consulta las pólizas vigentes de un cliente por su ID de clave foránea.
* **`findByStatus_DebeRetornarPolizasConEstado`**
  * *Acción:* Retorna las pólizas que se encuentren en estados específicos (`ACTIVE`, `EXPIRED`, etc.).
* **`findByEndDateBefore_DebeRetornarPolizasVencidas`**
  * *Acción:* Obtiene pólizas cuya fecha de finalización sea menor a la fecha actual (ej. evaluación de vencimientos).
* **`findByProductType_DebeRetornarPolizasSegunTipoProducto`**
  * *Acción:* Busca pólizas haciendo un join implícito con el tipo de producto contratado.

### 5. 🧩 [ClaimRepositoryIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/ClaimRepositoryIntegracionTest.java)
Evalúa los reclamos/siniestros. Al ser la entidad con mayor cantidad de relaciones, valida la integridad referencial y búsquedas complejas.

* **`saveAndFindById_DebePersistirReclamoCorrectamente`**
  * *Acción:* Registra un reclamo en H2 asignándole póliza, cliente, supervisor, y taller/socio.
  * *Verificación:* Valida que todas las claves foráneas persistan y mapeen los objetos correctos.
* **`findByPolicyId_DebeRetornarReclamosDePoliza`**
  * *Acción:* Obtiene todos los reclamos cargados bajo una misma póliza de seguros.
* **`findBySupervisorId_DebeRetornarReclamosAsignados`**
  * *Acción:* Retorna los reclamos bajo la tutela de un supervisor específico.
* **`findByReportedAtBetween_DebeRetornarReclamosEnFechas`**
  * *Acción:* Filtra siniestros reportados dentro de una ventana de tiempo definida.
* **`updateAndDelete_DebeActualizarYBorrarReclamo`**
  * *Acción:* Crea un reclamo, lo actualiza a estado aprobado, y finalmente lo elimina de la base de datos de pruebas.
  * *Verificación:* Comprueba que el objeto se elimine por completo de H2.

### 6. 🧩 [CrossRelationsIntegracionTest](file:///C:/Users/HP/Downloads/insurance-platform-springboot/insurance-platform-springboot/src/test/java/com/insurance_platform_springboot/repository/CrossRelationsIntegracionTest.java)
Test de flujo de integración total. Simula un caso de uso real completo uniendo todas las partes del modelo de base de datos relacional.

* **`fullIntegrationFlow_DebeNavegarElGrafoDeObjetosExitosamente`**
  * *Acción:*
    1. Persiste un rol de cliente y un rol de supervisor.
    2. Crea al cliente (`User`) y al supervisor (`User`).
    3. Registra un producto de Auto en el catálogo.
    4. Vende una póliza de seguro asociada al cliente y producto.
    5. Registra un taller aliado (`Partner`).
    6. Genera un reclamo (`Claim`) asociando la póliza, el cliente, el supervisor y el taller.
  * *Verificación:* Recupera el reclamo de la base de datos H2 por ID y recorre todo el grafo navegando por las asociaciones:
    * `claim -> policy -> product -> productType` (Verifica que sea `AUTO`).
    * `claim -> customer -> role -> name` (Verifica que sea `ROLE_USER`).
    * `claim -> supervisor -> role -> name` (Verifica que sea `ROLE_SUPERVISOR`).
    * `claim -> partner -> partnerName` (Verifica el nombre del taller).

---

## 🛠️ Cómo Ejecutar las Pruebas

Para correr todos los tests unitarios y de integración de forma local, utiliza el wrapper de Maven incluido en la raíz del proyecto:

```bash
# En Windows (PowerShell / CMD)
.\mvnw.cmd test

# En macOS / Linux
./mvnw test
```

Los resultados detallados se generan automáticamente en la carpeta `target/surefire-reports/`.
