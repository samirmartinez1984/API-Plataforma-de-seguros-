# Dockerfile para la aplicación Spring Boot de la Plataforma de Seguros
# Utiliza una estrategia de "multi-stage build" para crear una imagen final optimizada,
# ligera y segura para producción.

# =================================================================================
# ETAPA 1: BUILD (Construcción)
# - Usa una imagen completa de Maven y JDK para compilar el proyecto.
# - El resultado de esta etapa es el archivo .jar ejecutable.
# =================================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar primero los archivos de definición del proyecto (pom.xml y wrapper de Maven).
# Esto aprovecha el sistema de caché de capas de Docker: si estos archivos no cambian,
# Docker reutilizará la capa de dependencias descargadas, acelerando builds futuros.
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Otorgar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Descargar todas las dependencias del proyecto. El flag -B (batch mode) evita salidas interactivas.
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código fuente de la aplicación
COPY src ./src

# Compilar el proyecto, empaquetarlo en un .jar y omitir las pruebas.
# Las pruebas ya se deben haber ejecutado en un pipeline de CI/CD antes de esta etapa.
RUN ./mvnw clean package -DskipTests

# =================================================================================
# ETAPA 2: RUNTIME (Ejecución)
# - Usa una imagen mucho más ligera, solo con el Java Runtime Environment (JRE).
# - No contiene herramientas de compilación ni código fuente, solo lo necesario para ejecutar.
# =================================================================================
FROM eclipse-temurin:21-jre-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar únicamente el archivo .jar generado en la etapa 'builder'.
# Esto resulta en una imagen final pequeña y con una superficie de ataque reducida.
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecutará dentro del contenedor.
# Este es el puerto interno; al ejecutar el contenedor, se mapeará a un puerto del host.
EXPOSE 8080

# Comando que se ejecutará al iniciar el contenedor.
# Inicia la aplicación Spring Boot contenida en el .jar.
ENTRYPOINT ["java", "-jar", "app.jar"]