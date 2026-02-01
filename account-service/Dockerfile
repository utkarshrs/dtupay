FROM eclipse-temurin:21-jre
WORKDIR /app
COPY account-service/target/lib ./lib
COPY account-service/target/account-service-1.0-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
