# Stage 1: Build
# Usamos una imagen con Maven y JDK 25 para compilar el proyecto
FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /app

# optimiza la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos omitiendo los tests (los tests corren en el CI)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime
# Usamos solo el JRE 25 para minimizar la superficie de ataque y el peso de la imagen
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Extraemos el .jar compilado de la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto interno de Spring Boot
EXPOSE 8080

# Comando de arranque optimizado
ENTRYPOINT ["java", "-jar", "app.jar"]