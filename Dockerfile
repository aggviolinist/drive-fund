# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory
WORKDIR /app

# 1. Copy POM file ONLY
# This layer changes ONLY when dependencies change.
COPY pom.xml . 

# 2. Download dependencies
# Build the project, resulting in the JAR file in /target/
RUN mvn dependency:go-offline

# 3. Copy the rest of the source code
# This layer changes often, but it's AFTER the slow dependency step.
COPY src /app/src
RUN mvn clean install -DskipTests

# Stage 2: Copy the build to target folder, expose it and run it
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar savingsapp.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","savingsapp.jar" ]

