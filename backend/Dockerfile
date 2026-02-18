# Build
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# Run
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN adduser -D -s /bin/sh app \
    && apk add --no-cache su-exec

RUN mkdir -p /app/uploads && chown app:app /app/uploads

COPY --from=build /app/target/*.jar app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
