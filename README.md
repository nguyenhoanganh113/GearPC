# GearPC - E-Commerce Backend API

GearPC là dự án Backend RESTful API cho hệ thống thương mại điện tử chuyên cung cấp linh kiện và máy tính (PC), xây dựng trên nền tảng Spring Boot và kiến trúc Microservices/Monolith hiện đại.

## 🚀 Công Nghệ Sử Dụng

* **Ngôn ngữ:** Java (JDK 21)
* **Framework chính:** Spring Boot 4.1.1
* **Cơ sở dữ liệu:** PostgreSQL 16 (`postgres:16-alpine`)
* **Cache & Token Management:** Redis 7 (`redis:7-alpine`)
* **Bảo mật:** Spring Security + OAuth2 Resource Server
* **ORM:** Spring Data JPA / Hibernate
* **Data Validation:** Hibernate Validator
* **Quản lý Container:** Docker & Docker Compose

## 📋 Yêu Cầu Hệ Thống (Prerequisites)

1. **Java Development Kit (JDK):** Phù hợp với phiên bản cấu hình dự án.
2. **Docker Desktop:** Bắt buộc phải bật trước khi chạy app để Spring Boot tự mồi Database và Redis qua Docker Compose.
3. **IDE:** IntelliJ IDEA (khuyên dùng).

## 🛠 Hướng Dẫn Khởi Chạy (How to Run)

1. Mở ứng dụng **Docker Desktop**.
2. Mở dự án trong IntelliJ IDEA và chạy file `GearPCApplication.java` (hoặc dùng lệnh `./mvnw spring-boot:run` từ terminal).
3. Các dịch vụ sẽ tự động kích hoạt tại các cổng:
    * **API Service:** `http://localhost:8086`
    * **PostgreSQL Database:** `localhost:5435`
    * **Redis Cache:** `localhost:6378`

---

## 📚 Tài Liệu Tham Khảo (References)

* [Spring Web](https://docs.spring.io/spring-boot/4.1.1/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data Redis](https://docs.spring.io/spring-boot/4.1.1/reference/data/nosql.html#data.nosql.redis)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/4.1.1/reference/web/spring-security.html#web.security.oauth2.server)
* [Validation](https://docs.spring.io/spring-boot/4.1.1/reference/io/validation.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/4.1.1/reference/features/dev-services.html#features.dev-services.docker-compose)