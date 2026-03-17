FROM eclipse-temurin:21-jdk-jammy
COPY build/libs/*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]