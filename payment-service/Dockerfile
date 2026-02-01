FROM eclipse-temurin:21-jre
WORKDIR /app
COPY payment-service/target/lib ./lib
COPY payment-service/target/payment-service-1.0-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
