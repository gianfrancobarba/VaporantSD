# ===== Fase 1: build Maven =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copio solo i file necessari al build
COPY pom.xml .
COPY src ./src

# Compilo e creo il jar
RUN mvn clean package -DskipTests

# ===== Fase 2: runtime Java =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copio il jar generato nella fase di build
COPY --from=build /app/target/vaporant-0.0.1-SNAPSHOT.war app.war

# Espongo la porta su cui gira Vaporant 
EXPOSE 8080

# Comando di avvio dell'app
ENTRYPOINT ["java", "-jar", "app.war"]
