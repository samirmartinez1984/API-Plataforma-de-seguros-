# ---------- Etapa 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar el cache de capas de Docker
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------- Etapa 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos solo el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]