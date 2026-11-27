# Giai đoạn 1: Maven + JDK 21 để biên dịch và đóng gói API Gateway.
# Tên "build" dùng để lấy file JAR ở giai đoạn chạy bên dưới.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Thư mục làm việc trong container build.
WORKDIR /workspace

# Các đường dẫn COPY được tính từ build context.
# Build từ thư mục GearPC bằng `docker build -t gearpc-gateway ./api-gateway`
# thì build context là thư mục api-gateway.
# Chép Maven Wrapper và pom.xml trước source để cache dependencies riêng.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Bảo đảm Maven Wrapper có quyền thực thi trong Linux container.
RUN chmod +x mvnw

# Tải trước dependencies Maven. -B chạy không tương tác;
# -DskipTests bỏ qua việc chạy test trong lúc đóng gói image.
RUN ./mvnw -B -DskipTests dependency:go-offline

# Chép mã nguồn và tài nguyên của API Gateway vào giai đoạn build.
COPY src/ src/

# Biên dịch ứng dụng và đóng gói JAR vào target/.
RUN ./mvnw -B -DskipTests package

# Giai đoạn 2: image chạy ứng dụng, chỉ cần JRE 21.
FROM eclipse-temurin:21-jre

# Thư mục làm việc trong image chạy.
WORKDIR /app

# Chỉ chép JAR đã build sang image cuối.
# Cập nhật tên này nếu artifactId/version trong pom.xml thay đổi.
COPY --from=build /workspace/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Chạy bằng UID/GID thường, không chạy ứng dụng bằng root.
USER 10001:10001

# Cổng HTTP của API Gateway bên trong container.
# EXPOSE chỉ mô tả cổng; Compose cần publish nếu muốn host truy cập trực tiếp.
EXPOSE 9191

# Lệnh mặc định để khởi động gateway.
# Địa chỉ Eureka và các cấu hình môi trường nên truyền qua Compose.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
