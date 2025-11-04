FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN apt-get update && apt-get install -y findutils
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=builder /app/build/libs/*.jar /opt/app/spring-boot-application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/spring-boot-application.jar"]