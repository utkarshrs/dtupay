FROM eclipse-temurin:21-jre
WORKDIR /app
COPY facade/target/quarkus-app ./quarkus-app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-app/quarkus-run.jar"]
