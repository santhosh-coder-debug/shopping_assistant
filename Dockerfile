FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd -r -u 10001 appuser
COPY --from=build /app/target/shopping-assistant-1.0.0.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
