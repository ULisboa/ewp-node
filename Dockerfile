# Build and package
FROM maven:3-openjdk-11-slim as builder

WORKDIR /build

RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - &&\
    apt-get install -y nodejs

# Compile and package
COPY . .
RUN mvn -B package

# Deploy
FROM eclipse-temurin:11-jre

RUN apt-get update && apt-get install -y --no-install-recommends wget

# Install mermaid-cli for diagrams generation (requires node.js)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - &&\
    apt-get install -y nodejs
RUN npm install -g puppeteer @mermaid-js/mermaid-cli
COPY backend/puppeteer-config.json /opt/puppeteer-config.json

COPY --from=builder /build/delivery/target/ewp-node-*-full.jar /opt/app.jar

ENV SPRING_CONFIG_LOCATION=/config/application.yml
VOLUME /config

VOLUME /logs

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/opt/app.jar"]
