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

## 📌 Quy Ước Mã Trạng Thái (Business & Error Codes)

Dự án sử dụng chuẩn bọc dữ liệu chung (`ApiResponse`) nhằm đồng nhất cấu trúc JSON trả về. Các mã trạng thái (Business Codes) được quy hoạch theo từng module nghiệp vụ để dễ dàng truy vết trạng thái xử lý ở phía Frontend:

### 1. Mã lỗi chung hệ thống (Common Errors - Dải mã 1xxx)
| Mã Code    | HTTP Status       | Thông báo (Message)                                    | Ý nghĩa nghiệp vụ                                              |
|:-----------|:------------------|:-------------------------------------------------------|:---------------------------------------------------------------|
| **`1003`** | `400 Bad Request` | *Dữ liệu không hợp lệ hoặc vi phạm ràng buộc hệ thống* | Lỗi vi phạm toàn vẹn dữ liệu chung ở tầng Database/Validation. |

### 2. Module Brand (Thương Hiệu - Dải mã 2xxx)
Quy ước: Mã `21xx` dành cho các thao tác thành công, mã `20xx` dành cho các ngoại lệ/lỗi.

| Mã Code    | HTTP Status     | Thông báo (Message)                   | Phân loại                                                                               |
|:-----------|:----------------|:--------------------------------------|:----------------------------------------------------------------------------------------|
| **`2100`** | `200`           | *(Tùy thuộc vào thao tác thành công)* | **Thành công** - Áp dụng cho mọi thao tác truy vấn, thêm, sửa, xóa trên resource Brand. |
| **`2001`** | `404 Not Found` | *Thương hiệu không thể tìm thấy!*     | **Lỗi** - Xảy ra khi truy vấn/cập nhật/xóa một ID không tồn tại trong Database.         |
| **`2002`** | `409 Conflict`  | *Tên thương hiệu đã tồn tại*          | **Lỗi** - Xảy ra khi tạo mới hoặc cập nhật trùng tên với một thương hiệu khác.          |

### 3. Module Category (Danh Mục - Dải mã 22xx / 23xx)
Quy ước: Mã `23xx` dành cho các thao tác thành công, mã `22xx` dành cho các ngoại lệ/lỗi.

| Mã Code    | HTTP Status     | Thông báo (Message)                   | Phân loại                                                                                  |
|:-----------|:----------------|:--------------------------------------|:-------------------------------------------------------------------------------------------|
| **`2300`** | `200` / `201`   | *(Tùy thuộc vào thao tác thành công)* | **Thành công** - Áp dụng cho mọi thao tác truy vấn, thêm, sửa, xóa trên resource Category. |
| **`2201`** | `404 Not Found` | *Danh mục không thể tìm thấy!*        | **Lỗi** - Xảy ra khi truy vấn/cập nhật/xóa một ID danh mục không tồn tại trong Database.   |
| **`2202`** | `409 Conflict`  | *Tên danh mục đã tồn tại*             | **Lỗi** - Xảy ra khi tạo mới hoặc cập nhật trùng tên với một danh mục khác.                |
---

## 📚 Tài Liệu Tham Khảo (References)

* [Spring Web](https://docs.spring.io/spring-boot/4.1.1/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data Redis](https://docs.spring.io/spring-boot/4.1.1/reference/data/nosql.html#data.nosql.redis)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/4.1.1/reference/web/spring-security.html#web.security.oauth2.server)
* [Validation](https://docs.spring.io/spring-boot/4.1.1/reference/io/validation.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/4.1.1/reference/features/dev-services.html#features.dev-services.docker-compose)