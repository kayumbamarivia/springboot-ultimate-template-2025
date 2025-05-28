FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/fortress-*.jar fortress-app.jar

RUN chmod 750 /app && \
    addgroup --system javagroup && \
    adduser --system --ingroup javagroup javauser && \
    chown -R javauser:javagroup /app

USER javauser

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "fortress-app.jar"]