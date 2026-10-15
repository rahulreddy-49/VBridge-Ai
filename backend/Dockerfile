# Multi-stage Dockerfile for Spring Boot Backend
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root security user
RUN addgroup -S medlit && adduser -S medlit -G medlit

# Copy executable jar
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R medlit:medlit /app

USER medlit

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
