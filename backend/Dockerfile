# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Create directories for resources
RUN mkdir -p /app/uploads/images

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Copy resources if needed
COPY src/main/resources/application.properties /app/application.properties

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--server.address=0.0.0.0"] 
