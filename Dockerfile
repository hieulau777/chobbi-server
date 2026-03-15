## Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

COPY src ./src
RUN mvn -q -e -B clean package -DskipTests

## Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV JAVA_OPTS="-Xms256m -Xmx512m"

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

