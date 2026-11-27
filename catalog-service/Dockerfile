# Biên dịch catalog-service bằng Maven và JDK 21.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Tải dependencies trước để Docker có thể dùng lại cache khi source thay đổi.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

# Biên dịch source và đóng gói thành JAR.
COPY src/ src/
RUN ./mvnw -B -DskipTests package

# Image chạy chỉ cần JRE, không cần Maven hay source code.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/catalog-service-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
