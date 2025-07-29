# STEP 1: Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY server/ .
RUN ./mvnw clean package -DskipTests

# STEP 2: Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
