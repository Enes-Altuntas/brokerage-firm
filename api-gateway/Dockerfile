FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests=true

FROM eclipse-temurin:21-jre AS runtime
COPY --from=build /target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
