FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY infrastructure/pom.xml infrastructure/
COPY domain/pom.xml domain/
COPY application/pom.xml application/

COPY infrastructure /app/infrastructure
COPY domain /app/domain
COPY application /app/application

COPY application/src/main/resources/ /app/application/src/main/resources/

RUN mvn clean package -DskipTests

FROM openjdk:21 AS runtime
WORKDIR /app

COPY --from=build /app/application/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
