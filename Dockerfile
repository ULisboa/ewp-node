# Build and package
FROM maven:3-eclipse-temurin-17 AS builder

WORKDIR /build

ENV NODE_MAJOR=22
RUN apt-get update && apt-get install -y ca-certificates curl gnupg && \
  mkdir -p /etc/apt/keyrings && \
  (curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg) && \
  (echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list) && \
  apt-get update && apt-get install nodejs -y

COPY pom.xml pom.xml
COPY backend/pom.xml backend/pom.xml
COPY delivery/pom.xml delivery/pom.xml
COPY frontend/pom.xml frontend/pom.xml

# Download dependencies first
RUN mvn -pl backend,frontend dependency:resolve

# Disable NX build cache
ENV NX_SKIP_NX_CACHE=true

# Package modules individually
COPY frontend frontend
RUN mvn -pl frontend -B install

COPY backend backend
RUN mvn -pl backend -B install -Ddependency-check.skip=true

COPY delivery delivery
RUN mvn -B install -DskipTests=true -Ddependency-check.skip=true

# Deploy
FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y --no-install-recommends wget

COPY --from=builder /build/delivery/target/ewp-node-*-full.jar /opt/app.jar

ENV SPRING_CONFIG_LOCATION=/config/application.yml
VOLUME /config

VOLUME /logs

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/opt/app.jar"]
