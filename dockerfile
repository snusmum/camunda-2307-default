FROM openjdk:8-jdk-alpine
RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*
ARG JAR_FILE=/target/camunda-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]