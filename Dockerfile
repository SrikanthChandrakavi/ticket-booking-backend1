# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy project files
COPY . .

# Build the Spring Boot JAR (skip tests for faster build)
RUN --mount=type=cache,target=/root/.m2 mvn clean install package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose backend port
EXPOSE 8081

# Run the application (exec form → Java is PID 1)
ENTRYPOINT ["java", "-jar", "app.jar"]