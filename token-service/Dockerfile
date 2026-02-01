FROM eclipse-temurin:21-jre
WORKDIR /app
COPY token-service/target/lib ./lib
COPY token-service/target/token-service-1.0-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

