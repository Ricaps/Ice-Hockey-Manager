FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn

COPY world-list-service/pom.xml world-list-service/pom.xml
COPY world-list-service/src world-list-service/src

COPY team-service/pom.xml team-service/pom.xml
COPY team-service/src team-service/src

COPY game-service/pom.xml game-service/pom.xml
COPY game-service/src game-service/src

COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src

COPY common-library/pom.xml common-library/pom.xml
COPY common-library/src common-library/src

COPY oauth-client/pom.xml oauth-client/pom.xml
COPY oauth-client/src oauth-client/src

RUN chmod +x mvnw
RUN ./mvnw clean package -Dskip.unit.tests=true -Dskip.integration.tests=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SERVICE_NAME
ENV SPRING_CONFIG_ADDITIONAL_LOCATION="./etc/${SERVICE_NAME}.yaml"

COPY --from=builder "/app/${SERVICE_NAME}/target/${SERVICE_NAME}.jar" /app/service.jar

ENTRYPOINT ["java", "-jar", "service.jar"]