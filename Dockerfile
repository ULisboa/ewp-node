# Build and package
FROM maven:3.6.3-slim as builder

WORKDIR /build

# Compile and package
COPY . .
RUN mvn -B package

# Deploy
FROM openjdk:11.0-jre-slim

RUN apt-get update && apt-get install -y --no-install-recommends wget

COPY --from=builder /build/ewp-node/target/*.jar /opt/app.jar

ENV SPRING_CONFIG_LOCATION=/config/application.yml
VOLUME /config

VOLUME /logs

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/opt/app.jar"]
