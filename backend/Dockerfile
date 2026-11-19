FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 --create-home --home-dir /app appuser
COPY --from=build /app/target/shopping-assistant-1.0.0.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-XX:+ExitOnOutOfMemoryError","-jar","/app/app.jar"]
