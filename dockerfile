# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy .env file - will be loaded by spring-dotenv
COPY .env /app/.env

# Expose port from .env (default fallback)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]