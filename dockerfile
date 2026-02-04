# ===============================
# Build stage
# ===============================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build -x test --no-daemon


# ===============================
# Runtime stage
# ===============================
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

RUN apt-get update && apt-get install -y bash && rm -rf /var/lib/apt/lists/*

CMD ["bash", "-c", "set -a; source /etc/secrets/.env; set +a; exec java $JAVA_OPTS -jar app.jar"]
