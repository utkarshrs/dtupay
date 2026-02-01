FROM eclipse-temurin:21-jre
WORKDIR /app
COPY report-service/target/lib ./lib
COPY report-service/target/report-service-1.0-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
