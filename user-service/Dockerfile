# Giai đoạn 1: Maven + JDK 21 để biên dịch và đóng gói user-service.
# Tên "build" dùng để tham chiếu tới giai đoạn này ở phần image chạy.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Thư mục làm việc bên trong container build.
WORKDIR /workspace

# Các đường dẫn nguồn được tính từ build context.
# Build từ thư mục gốc GearPC bằng `docker build -t gearpc-user ./user-service`
# thì build context là thư mục user-service.
# Chép cấu hình Maven trước source để Docker có thể cache bước tải dependencies.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Bảo đảm Maven Wrapper có quyền chạy trong Linux container.
RUN chmod +x mvnw

# Tải trước dependencies Maven. -B là chế độ không tương tác;
# -DskipTests bỏ qua việc chạy test trong lúc đóng gói image.
RUN ./mvnw -B -DskipTests dependency:go-offline

# Chép mã nguồn và tài nguyên của user-service vào giai đoạn build.
COPY src/ src/

# Biên dịch ứng dụng và tạo JAR trong target/.
RUN ./mvnw -B -DskipTests package

# Giai đoạn 2: image tối thiểu để chạy ứng dụng, chỉ cần JRE 21.
FROM eclipse-temurin:21-jre

# Thư mục làm việc bên trong image chạy.
WORKDIR /app

# Chép duy nhất JAR đã build từ giai đoạn trước.
# Nếu artifactId/version trong pom.xml thay đổi, cập nhật tên JAR ở đây.
COPY --from=build /workspace/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Chạy ứng dụng bằng user thường thay vì root để giới hạn quyền trong container.
USER 10001:10001

# Cổng HTTP user-service lắng nghe bên trong container.
# EXPOSE chỉ mô tả cổng; Compose cần khai báo ports nếu muốn mở ra host.
EXPOSE 8087

# Lệnh khởi động mặc định của container.
# Cấu hình DB, Redis, Eureka và Keycloak nên được truyền qua Compose environment,
# không nên gắn địa chỉ môi trường cụ thể vào image.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
