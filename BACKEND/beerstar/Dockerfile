# === BUILD STAGE ===
# Usamos una imagen base con Maven 3.9.9 y Eclipse Temurin OpenJDK 21 para construir nuestra aplicación.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia los archivos del proyecto Maven (pom.xml y src).
COPY pom.xml .
COPY src ./src

# Construye la aplicación Spring Boot.
RUN mvn clean package -DskipTests

# === RUN STAGE ===
# Usamos una imagen más ligera con el JRE de Eclipse Temurin 21 basado en Alpine.
FROM eclipse-temurin:21-jre-alpine

# Copia el JAR ejecutable desde la etapa de construcción.
# ¡CORREGIDO AQUÍ! Especificamos la ruta completa y eliminamos la variable ARG para mayor claridad.
COPY --from=build /app/target/beerstar-0.0.1-SNAPSHOT.jar app.jar

# Define el comando para ejecutar la aplicación.
ENTRYPOINT ["java", "-jar", "/app.jar"]