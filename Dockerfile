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

# Usuario sin privilegios
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /app/target/orbix-api-0.0.1-SNAPSHOT.jar app.jar

# Conexion a la base de datos (Postgres en el mismo servidor)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/agrolab
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=Agrolab(2026)

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
