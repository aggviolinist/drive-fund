# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory
WORKDIR /app
# Copy the project files into the build stage
COPY . . 
# Build the project, resulting in the JAR file in /target/
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar savingsapp.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","savingsapp.jar" ]

