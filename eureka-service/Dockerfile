# Giai đoạn 1: dùng Maven và JDK 21 để biên dịch, đóng gói ứng dụng.
# Tên "build" cho phép giai đoạn sau lấy JAR từ đây.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Các lệnh RUN/COPY tiếp theo mặc định làm việc trong thư mục này.
WORKDIR /workspace

# Docker lấy các đường dẫn nguồn từ build context.
# Khi build bằng `docker build -t gearpc-eureka ./eureka-service`,
# build context chính là thư mục eureka-service.
# Sao chép cấu hình Maven Wrapper, script mvnw và pom.xml trước mã nguồn
# để Docker có thể dùng lại lớp tải dependencies khi chỉ có source thay đổi.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Đảm bảo script Maven Wrapper có quyền thực thi trong Linux container.
RUN chmod +x mvnw

# Tải trước dependencies Maven để bước package phía dưới ít phải tải lại.
# -B chạy Maven không tương tác; -DskipTests bỏ qua chạy test trong lúc build.
RUN ./mvnw -B -DskipTests dependency:go-offline

# Sao chép mã nguồn Java và tài nguyên của ứng dụng vào container build.
COPY src/ src/

# Biên dịch và đóng gói ứng dụng thành file JAR trong target/.
RUN ./mvnw -B -DskipTests package

# Giai đoạn 2: image chạy ứng dụng, chỉ cần JRE; không cần Maven hay JDK đầy đủ.
# Tách giai đoạn giúp image cuối nhỏ và chỉ chứa thành phần cần để chạy.
FROM eclipse-temurin:21-jre

# Thư mục làm việc trong image chạy.
WORKDIR /app

# Chỉ chép JAR từ giai đoạn build sang image chạy, không chép source/Maven.
# Tên và version file phải khớp artifactId/version trong eureka-service/pom.xml.
COPY --from=build /workspace/target/eureka-service-0.0.1-SNAPSHOT.jar app.jar

# Chạy Java bằng UID/GID thường thay vì root để giới hạn quyền của ứng dụng.
USER 10001:10001

# Tài liệu hóa cổng Eureka trong container. EXPOSE không tự mở cổng ra máy host;
# cần khai báo ports trong Compose (ví dụ "8761:8761") để truy cập từ host.
EXPOSE 8761

# Lệnh mặc định khi container khởi động: chạy ứng dụng Spring Boot từ JAR.
# Dạng JSON giúp Java nhận tín hiệu dừng trực tiếp khi container được stop.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
