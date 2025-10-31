# Build stage
FROM maven:3.9.3-eclipse-temurin-17 as build
WORKDIR /workspace
COPY pom.xml mvnw* ./
COPY .mvn .mvn
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src src
RUN mvn -B -q -e -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/rag-chat-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
