FROM alpine:latest
RUN apk add --no-cache openjdk21
WORKDIR /app
COPY target/sem-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]