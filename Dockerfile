# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy source code and build application
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK image to run the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from the first stage
COPY --from=build /app/target/temple-billing-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
