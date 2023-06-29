FROM eclipse-temurin:17-alpine
ADD target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8080
