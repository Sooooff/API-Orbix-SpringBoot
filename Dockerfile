# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar primero el descriptor para aprovechar la cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el codigo fuente y compilar el jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# curl para el HEALTHCHECK y usuario sin privilegios
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /app/target/orbix-api-0.0.1-SNAPSHOT.jar app.jar

# La conexion a la base de datos se inyecta por variables de entorno
# (ver docker-compose.yml / .env), no se hornea en la imagen.

EXPOSE 8082

# Docker marca el contenedor como "healthy" cuando responde el endpoint de Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8082/api/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
