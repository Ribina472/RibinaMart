FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package

EXPOSE 8085

CMD ["mvn", "-B", "exec:java"]
