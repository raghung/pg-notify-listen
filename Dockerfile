# Start with a base image containing Java runtime (JDK 17)
# FROM openjdk:17-slim

# Start with a base image containing Java runtime (Eclipse Temurin JRE 17)
FROM eclipse-temurin:17-jre-focal

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/*.jar

# Copy the application's jar file to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]

# docker run --name pg-notify-demo -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://my-postgres:5432/demo_db -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root pg-notify-listen-app
