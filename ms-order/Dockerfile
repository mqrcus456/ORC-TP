FROM eclipse-temurin:21
WORKDIR /app
COPY target/service-order-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]  