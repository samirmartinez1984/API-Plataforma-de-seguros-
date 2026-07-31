# Insurance Platform Spring Boot

Proyecto de ejemplo para una plataforma de seguros. Esta guía rápida explica cómo ver la documentación OpenAPI (Swagger)
y generar la JavaDoc del proyecto.

Requisitos:
- Java 21
- Maven

Cómo ejecutar la aplicación:

```powershell
mvn spring-boot:run
```

Swagger / OpenAPI:
- Con la dependencia `springdoc-openapi-starter-webmvc-ui` incluida, la UI de Swagger estará disponible en:
  - http://localhost:8080/swagger-ui.html
  - o http://localhost:8080/swagger-ui/index.html

Generar JavaDoc:

```powershell
mvn javadoc:javadoc
# Los artefactos generados se encuentran en target/site/apidocs
```

Notas:
- En este proyecto aún no hay controladores REST; la documentación OpenAPI listará los endpoints cuando agregues `@RestController`.
- Se ha añadido `OpenApiConfig` para configurar título/versión/descripcion.

Migraciones / Base de datos:
- El proyecto usa Flyway para gestionar migraciones. El primer script es `src/main/resources/db/migration/V1__Init.sql`.
- Ese script crea las tablas principales: `roles`, `users`, `catalog_products`, `policies`, `partners`, `claims`.
- Comentarios y descripción del script están incluidos dentro del propio SQL.

Si necesitas ejecutar las migraciones manualmente contra una BD local, ajusta `application.properties
` con la cadena de conexión y levanta la app; Flyway ejecutará las migraciones automáticamente al arranque.

