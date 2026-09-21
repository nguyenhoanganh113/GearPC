# GearPC Backend API

GearPC là REST API cho hệ thống thương mại điện tử linh kiện máy tính. Dự án hiện được tổ chức theo hướng modular monolith; module Catalog đã triển khai các nghiệp vụ quản lý thương hiệu, danh mục và sản phẩm.

## Công nghệ

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA và Hibernate
- Jakarta Validation
- PostgreSQL 16
- Redis 7
- MapStruct và Lombok
- Maven Wrapper
- Docker Compose

## Cấu trúc chính

```text
src/main/java/com/gearpc
├── catalog
│   ├── application     # DTO, mapper và service
│   ├── controller      # REST endpoints
│   ├── domain          # Entity và value object
│   └── repository      # JPA repository và specification
├── common
│   ├── annotation      # Custom validation
│   ├── dto             # API response và pagination
│   ├── entity          # Base entity và auditing
│   ├── exception       # ErrorCode và global exception handler
│   └── util            # Tiện ích dùng chung
├── identity            # Khung module định danh
├── order               # Khung module đơn hàng
├── payment             # Khung module thanh toán
└── infrastructure      # Khung tích hợp hạ tầng
```

## Yêu cầu

- JDK 21
- Docker Desktop hoặc Docker Engine có Docker Compose

Không cần cài Maven, PostgreSQL hoặc Redis trực tiếp trên máy vì dự án đã có Maven Wrapper và `compose.yaml`.

## Khởi chạy

### Cách 1: để Spring Boot quản lý Docker Compose

Đảm bảo Docker đang chạy, sau đó:

```bash
./mvnw spring-boot:run
```

Spring Boot sẽ đọc `compose.yaml`, khởi động PostgreSQL và Redis cùng ứng dụng, sau đó dừng các container khi ứng dụng dừng.

### Cách 2: khởi động hạ tầng riêng

```bash
docker compose up -d
./mvnw spring-boot:run
```

Các dịch vụ mặc định:

| Dịch vụ | Địa chỉ |
|---|---|
| API | `http://localhost:8086` |
| PostgreSQL | `localhost:5435` |
| Redis | `localhost:6378` |

Cấu hình phát triển hiện nằm trong `src/main/resources/application.yaml`:

```text
Database: gearpc_db
Username: postgres
Password: 123456
```

Các thông tin này chỉ phù hợp với môi trường phát triển cục bộ. Không sử dụng trực tiếp cho production.

## Kiểm tra dự án

```bash
./mvnw test
```

Chỉ compile mà không chạy test:

```bash
./mvnw -DskipTests compile
```

## API Catalog

Base URL:

```text
http://localhost:8086/api/v1/admin
```

### Brand

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/brands` | Tạo thương hiệu |
| `GET` | `/brands/search` | Tìm kiếm và phân trang thương hiệu |
| `GET` | `/brands/options` | Lấy thương hiệu đang hoạt động, sắp xếp theo tên |
| `GET` | `/brands/{id}` | Lấy chi tiết thương hiệu |
| `PUT` | `/brands/{id}` | Cập nhật thương hiệu |
| `PATCH` | `/brands/{id}/status?active=true` | Thay đổi trạng thái hoạt động |
| `DELETE` | `/brands/{id}` | Xóa thương hiệu |

Mỗi phần tử từ `/brands/options` gồm `id`, `name` và `slug`.

### Category

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/categories` | Tạo danh mục |
| `GET` | `/categories/search` | Tìm kiếm và phân trang danh mục |
| `GET` | `/categories/options` | Lấy danh mục đang hoạt động, sắp xếp theo tên |
| `PUT` | `/categories/{id}` | Cập nhật danh mục |
| `PATCH` | `/categories/{id}/status?active=true` | Thay đổi trạng thái hoạt động |
| `DELETE` | `/categories/{id}` | Xóa danh mục |

### Product

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/products` | Tạo sản phẩm mới với trạng thái mặc định `INACTIVE` |
| `GET` | `/products/{id}` | Lấy chi tiết sản phẩm |
| `GET` | `/products/search` | Tìm kiếm, lọc và phân trang sản phẩm |
| `PUT` | `/products/{id}` | Cập nhật một phần thông tin sản phẩm; các field không gửi lên được giữ nguyên |
| `PATCH` | `/products/{id}/status` | Cập nhật trạng thái `ACTIVE` hoặc `INACTIVE` |
| `DELETE` | `/products/{id}` | Soft delete sản phẩm bằng cách chuyển sang `INACTIVE` và ghi nhận `deletedAt` |

#### Tạo sản phẩm

```http
POST /api/v1/admin/products
Content-Type: application/json
```

```json
{
  "name": "ASUS ROG Strix GeForce RTX 5080",
  "sku": "ROG-STRIX-RTX5080-O16G",
  "description": "Card đồ họa ASUS ROG Strix",
  "price": 42990000,
  "stockQuantity": 10,
  "images": "https://example.com/rtx-5080.jpg",
  "categoryId": "550e8400-e29b-41d4-a716-446655440000",
  "brandId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
}
```

Các field bắt buộc gồm `name`, `price` (lớn hơn `0`), `stockQuantity` (không âm), `categoryId` và `brandId`. `sku`, `description` và `images` là tùy chọn. Tên hoặc SKU trùng với sản phẩm đã tồn tại trả lỗi `2402`.

#### Tìm kiếm sản phẩm

`GET /products/search` nhận các query parameter sau:

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `keyword` | `string` | Từ khóa tìm kiếm, tối đa 150 ký tự |
| `categoryId` | `UUID` | Lọc theo danh mục |
| `brandId` | `UUID` | Lọc theo thương hiệu |
| `productStatus` | `enum` | `ACTIVE` hoặc `INACTIVE` |
| `minPrice` | `decimal` | Giá tối thiểu, không âm |
| `maxPrice` | `decimal` | Giá tối đa, không âm và không nhỏ hơn `minPrice` |
| `inStock` | `boolean` | `true` để chỉ lấy sản phẩm còn hàng |
| `sortBy` | `enum` | `NAME_ASC`, `NAME_DESC`, `PRICE_ASC`, `PRICE_DESC`, `CREATED_AT_ASC` hoặc `CREATED_AT_DESC` |
| `page` | `integer` | Số trang, bắt đầu từ `1` |
| `size` | `integer` | Số phần tử mỗi trang, mặc định `15` |
| `sort` | `string` | Sắp xếp chuẩn Spring Data, ví dụ `price,asc`; mặc định `price,desc` khi không có `sortBy` |

Khi có `sortBy`, giá trị này được ưu tiên hơn `sort`. Kết quả còn được sắp xếp phụ theo `id` giảm dần để giữ thứ tự ổn định.

Ví dụ:

```http
GET /api/v1/admin/products/search?keyword=asus&productStatus=ACTIVE&minPrice=1000000&maxPrice=50000000&inStock=true&sortBy=PRICE_ASC&page=1&size=15
```

#### Cập nhật sản phẩm

`PUT /products/{id}` nhận JSON với các field tùy chọn: `name`, `sku`, `description`, `price`, `stockQuantity`, `images`, `categoryId` và `brandId`. Field không xuất hiện hoặc chuỗi rỗng sau khi loại khoảng trắng sẽ không thay đổi dữ liệu hiện tại.

```http
PUT /api/v1/admin/products/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
```

```json
{
  "price": 41990000,
  "stockQuantity": 8
}
```

#### Cập nhật trạng thái

```http
PATCH /api/v1/admin/products/550e8400-e29b-41d4-a716-446655440000/status
Content-Type: application/json
```

```json
{
  "productStatus": "ACTIVE"
}
```

Giá trị trạng thái không phân biệt chữ hoa/chữ thường. Giá trị ngoài `ACTIVE` và `INACTIVE` trả lỗi `2403`.

`DELETE /products/{id}` trả lỗi `2404` nếu sản phẩm đã ở trạng thái `INACTIVE`.

Các endpoint tìm kiếm hỗ trợ tham số phân trang chuẩn của Spring Data như `page`, `size` và `sort`. Cấu hình hiện tại sử dụng số trang bắt đầu từ `1`.

Ví dụ:

```http
GET /api/v1/admin/brands/search?keyword=asus&active=true&page=1&size=15
```

## Cấu trúc response

Response thành công được bọc bởi `ApiResponse<T>`:

```json
{
  "code": "200",
  "message": "Thành công",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "ASUS"
  }
}
```

Các endpoint tạo Brand, Category và Product hiện dùng mã kết quả `201` trong body:

```json
{
  "code": "201",
  "message": "Tạo mới thành công",
  "data": {}
}
```

`code` trong body là mã kết quả/nghiệp vụ. HTTP status vẫn được trả riêng ở status line của response. Với implementation hiện tại, các method trong controller trả trực tiếp `ApiResponse` nên cả thao tác tạo và xóa cũng trả HTTP `200 OK`; mã `201` của thao tác tạo chỉ nằm trong body.

### Response phân trang

```json
{
  "code": "200",
  "message": "Thành công",
  "data": {
    "pageNo": 1,
    "pageSize": 15,
    "totalElements": 30,
    "totalPages": 2,
    "first": true,
    "last": false,
    "content": []
  }
}
```

## Xử lý lỗi

`GlobalExceptionHandler` chuyển lỗi nghiệp vụ, lỗi validation, JSON không hợp lệ và lỗi ràng buộc dữ liệu thành `ApiResponse` với HTTP status phù hợp.

Ví dụ không tìm thấy thương hiệu:

```http
HTTP/1.1 404 Not Found
```

```json
{
  "code": "2001",
  "message": "Thương hiệu không thể tìm thấy!"
}
```

Khi request vi phạm annotation validation, `data` chứa lỗi theo từng field:

```json
{
  "code": "1001",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "data": [
    {
      "field": "name",
      "message": "Name is required"
    }
  ]
}
```

## Mã lỗi nghiệp vụ

| Code | HTTP status | Ý nghĩa |
|---|---|---|
| `1001` | `400 Bad Request` | Dữ liệu đầu vào không hợp lệ |
| `1002` | `400 Bad Request` | JSON không hợp lệ hoặc không đọc được |
| `1003` | `400 Bad Request` | Vi phạm ràng buộc hoặc tính toàn vẹn dữ liệu |
| `1004` | `400 Bad Request` | Khoảng giá không hợp lệ |
| `2001` | `404 Not Found` | Không tìm thấy thương hiệu |
| `2002` | `409 Conflict` | Tên thương hiệu đã tồn tại |
| `2201` | `404 Not Found` | Không tìm thấy danh mục |
| `2202` | `409 Conflict` | Tên danh mục đã tồn tại |
| `2401` | `404 Not Found` | Không tìm thấy sản phẩm |
| `2402` | `409 Conflict` | Sản phẩm đã tồn tại |
| `2403` | `400 Bad Request` | Trạng thái sản phẩm không hợp lệ |
| `2404` | `400 Bad Request` | Sản phẩm đã bị xóa |

## Ghi chú phát triển

- Hibernate đang dùng `ddl-auto: update`; nên chuyển sang Flyway hoặc Liquibase trước khi triển khai production.
- `open-in-view` đã tắt, vì vậy dữ liệu cần thiết nên được ánh xạ sang DTO trong service.
- Các module Identity, Order, Payment và một số tích hợp hạ tầng hiện mới là cấu trúc chuẩn bị cho phát triển tiếp theo.
