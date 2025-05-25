FROM eclipse-temurin:21-jdk-alpine

EXPOSE 8083

ADD target/notification-service.jar notification-service.jar

ENTRYPOINT [ "java", "-jar", "/notification-service.jar" ]