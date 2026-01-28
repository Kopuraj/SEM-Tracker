# Stage 1: Build with Maven and Java 21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
# Download dependencies (this layer will be cached)
RUN mvn dependency:resolve dependency:resolve-plugins -B

# Copy source code
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
