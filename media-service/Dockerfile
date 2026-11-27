# Giai đoạn 1: Maven + JDK 21 để biên dịch và đóng gói media-service.
# Tên "build" dùng để tham chiếu giai đoạn này trong phần image chạy.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Thư mục làm việc bên trong container build.
WORKDIR /workspace

# Các đường dẫn nguồn được tính từ build context.
# Build từ thư mục gốc GearPC bằng `docker build -t gearpc-media ./media-service`
# thì build context là thư mục media-service.
# Chép cấu hình Maven trước source để Docker có thể cache bước tải dependencies.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Bảo đảm Maven Wrapper có quyền chạy trong Linux container.
RUN chmod +x mvnw

# Tải trước dependencies Maven. -B chạy không tương tác;
# -DskipTests bỏ qua việc chạy test trong lúc đóng gói image.
RUN ./mvnw -B -DskipTests dependency:go-offline

# Chép mã nguồn Java và tài nguyên của media-service vào giai đoạn build.
COPY src/ src/

# Biên dịch ứng dụng và tạo JAR trong target/.
RUN ./mvnw -B -DskipTests package

# Giai đoạn 2: image chạy ứng dụng, chỉ cần JRE 21.
FROM eclipse-temurin:21-jre

# Thư mục làm việc bên trong image chạy.
WORKDIR /app

# Chép duy nhất JAR từ giai đoạn build; source và Maven không vào image cuối.
# Nếu artifactId/version trong pom.xml đổi, cập nhật tên JAR này.
COPY --from=build /workspace/target/media-service-0.0.1-SNAPSHOT.jar app.jar

# Chạy ứng dụng bằng UID/GID thường thay vì root.
USER 10001:10001

# Cổng HTTP của media-service bên trong container.
# EXPOSE không tự publish cổng ra máy host; cấu hình ports trong Compose nếu cần.
EXPOSE 8085

# Lệnh mặc định khi container chạy.
# URL database, MinIO và Eureka nên được truyền qua Compose environment.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
