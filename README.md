# GearPC Backend API

GearPC là REST API cho hệ thống thương mại điện tử linh kiện máy tính. Dự án được tổ chức theo hướng modular monolith. Module Catalog hiện hỗ trợ thương hiệu, danh mục, sản phẩm, định nghĩa thuộc tính và quan hệ thuộc tính của danh mục.

## Công nghệ

- Java 21, Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Hibernate
- Jakarta Validation, MapStruct, Lombok
- PostgreSQL 16, Redis 7
- Maven Wrapper, Docker Compose

## Cấu trúc dự án

```text
src/main/java/com/gearpc
├── catalog
│   ├── application     # DTO, mapper và service
│   ├── controller      # REST controller
│   ├── domain          # Entity, composite key và enum
│   └── repository      # JPA repository và specification
├── common              # Response, validation, auditing và exception
├── identity
├── order
├── payment
└── infrastructure
```

## Khởi chạy

Yêu cầu JDK 21 và Docker Desktop hoặc Docker Engine có Docker Compose.

Để Spring Boot tự quản lý các container:

```bash
./mvnw spring-boot:run
```

Hoặc khởi động hạ tầng riêng:

```bash
docker compose up -d
./mvnw spring-boot:run
```

| Dịch vụ | Địa chỉ mặc định |
|---|---|
| API | `http://localhost:8086` |
| PostgreSQL | `localhost:5435` |
| Redis | `localhost:6378` |

Cấu hình database phát triển là `gearpc_db`, user `postgres`, password `123456`. Không sử dụng trực tiếp cấu hình này cho production.

## Kiểm tra

```bash
./mvnw test
./mvnw -DskipTests compile
```

## Quy ước API

Base URL:

```text
http://localhost:8086/api/v1/admin
```

Các endpoint search dùng số trang bắt đầu từ `1`. Tham số phân trang chung:

| Tham số | Mô tả |
|---|---|
| `page` | Số trang |
| `size` | Số phần tử mỗi trang, mặc định `15` |
| `sort` | Định dạng `field,direction`, ví dụ `name,asc` |

## Brand API

| Method | Endpoint | HTTP | Mô tả |
|---|---|---|---|
| `POST` | `/brands` | `201` | Tạo thương hiệu |
| `GET` | `/brands/{id}` | `200` | Lấy chi tiết |
| `GET` | `/brands/search` | `200` | Tìm theo `keyword`, `active` và phân trang |
| `GET` | `/brands/options` | `200` | Lấy thương hiệu active, chưa bị xóa |
| `PUT` | `/brands/{id}` | `200` | Cập nhật |
| `PATCH` | `/brands/{id}/status?active=true` | `200` | Đổi trạng thái |
| `DELETE` | `/brands/{id}` | `204` | Soft delete |

## Category API

| Method | Endpoint | HTTP | Mô tả |
|---|---|---|---|
| `POST` | `/categories` | `201` | Tạo danh mục |
| `GET` | `/categories/{id}` | `200` | Lấy chi tiết |
| `GET` | `/categories/search` | `200` | Tìm theo `keyword`, `active` và phân trang |
| `GET` | `/categories/options` | `200` | Lấy danh mục active, chưa bị xóa |
| `PUT` | `/categories/{id}` | `200` | Cập nhật |
| `PATCH` | `/categories/{id}/status?active=true` | `200` | Đổi trạng thái |
| `DELETE` | `/categories/{id}` | `204` | Soft delete |
| `POST` | `/categories/{categoryId}/attributes` | `201` | Gán thuộc tính cho danh mục |
| `GET` | `/categories/{categoryId}/attributes` | `200` | Lấy thuộc tính của danh mục |
| `PATCH` | `/categories/{categoryId}/attributes/{attributeDefinitionId}` | `200` | Cập nhật trạng thái bắt buộc |
| `DELETE` | `/categories/{categoryId}/attributes/{attributeDefinitionId}` | `204` | Gỡ thuộc tính khỏi danh mục |

Ví dụ gán thuộc tính:

```http
POST /api/v1/admin/categories/550e8400-e29b-41d4-a716-446655440000/attributes
Content-Type: application/json
```

```json
{
  "attributeDefinitionId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
  "required": true
}
```

`required` cho biết sản phẩm thuộc danh mục có bắt buộc cung cấp giá trị cho thuộc tính hay không. Một thuộc tính không thể được gán lặp lại cho cùng một danh mục.

Payload cập nhật trạng thái bắt buộc:

```json
{
  "required": false
}
```

## Product API

| Method | Endpoint | HTTP | Mô tả |
|---|---|---|---|
| `POST` | `/products` | `200` | Tạo sản phẩm, mặc định `INACTIVE` |
| `GET` | `/products/{id}` | `200` | Lấy chi tiết |
| `GET` | `/products/search` | `200` | Tìm kiếm, lọc và phân trang |
| `PUT` | `/products/{id}` | `200` | Cập nhật các field được gửi lên |
| `PATCH` | `/products/{id}/status` | `200` | Cập nhật trạng thái |
| `DELETE` | `/products/{id}` | `200` | Chuyển sang `INACTIVE` và ghi `deletedAt` |
| `GET` | `/products/{productId}/attributes` | `200` | Lấy giá trị thuộc tính của sản phẩm |
| `PUT` | `/products/{productId}/attributes` | `200` | Đồng bộ toàn bộ giá trị thuộc tính |

### Tạo sản phẩm

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

`name`, `price`, `stockQuantity`, `categoryId` và `brandId` là bắt buộc. Giá phải lớn hơn `0`; tồn kho không được âm.

### Tìm kiếm sản phẩm

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `keyword` | `string` | Tìm theo tên hoặc SKU, tối đa 150 ký tự |
| `categoryId` | `UUID` | Lọc theo danh mục |
| `brandId` | `UUID` | Lọc theo thương hiệu |
| `productStatus` | `enum` | `ACTIVE` hoặc `INACTIVE` |
| `minPrice`, `maxPrice` | `decimal` | Khoảng giá không âm |
| `inStock` | `boolean` | `true`: còn hàng; `false`: hết hàng |
| `sortBy` | `enum` | `NAME_ASC`, `NAME_DESC`, `PRICE_ASC`, `PRICE_DESC`, `CREATED_AT_ASC`, `CREATED_AT_DESC` |

```http
GET /api/v1/admin/products/search?keyword=asus&productStatus=ACTIVE&minPrice=1000000&maxPrice=50000000&inStock=true&sortBy=PRICE_ASC&page=1&size=15
```

Khi có `sortBy`, giá trị này được ưu tiên hơn `sort`. Search luôn loại sản phẩm có `deletedAt` khác `null`.

Payload đổi trạng thái:

```json
{
  "productStatus": "ACTIVE"
}
```

### Giá trị thuộc tính sản phẩm

`PUT /products/{productId}/attributes` đồng bộ toàn bộ tập giá trị trong một transaction. Giá trị không còn xuất hiện trong request sẽ bị xóa; giá trị đã tồn tại được cập nhật; giá trị mới được thêm vào.

```http
PUT /api/v1/admin/products/550e8400-e29b-41d4-a716-446655440000/attributes
Content-Type: application/json
```

```json
{
  "attributes": [
    {
      "attributeDefinitionId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "value": "32"
    },
    {
      "attributeDefinitionId": "6ba7b811-9dad-11d1-80b4-00c04fd430c8",
      "value": "true"
    }
  ]
}
```

Quy tắc validation:

- Product và category của product phải chưa bị soft delete.
- Attribute phải active, chưa bị soft delete và đã được gán cho category.
- Không được gửi trùng `attributeDefinitionId`.
- Phải gửi đủ các attribute có `required=true`.
- `NUMBER` phải là số hợp lệ, `BOOLEAN` nhận `true` hoặc `false`, `DATE` dùng ISO `yyyy-MM-dd`.
- `TEXT` và `SELECT` hiện nhận chuỗi không rỗng; tập option cho `SELECT` chưa được quản lý trong model hiện tại.
- Có thể gửi mảng rỗng để xóa toàn bộ giá trị nếu category không có attribute bắt buộc.

`GET /products/{productId}/attributes` trả toàn bộ Attribute Definition active, chưa soft delete đã gán cho category. Attribute chưa có giá trị vẫn xuất hiện với `value: null`; field `required` cho biết input bắt buộc trên UI.

Khi request có một hoặc nhiều attribute không hợp lệ, API trả `400 Bad Request` với mã `3005`. Mỗi phần tử trong `data` xác định attribute bị lỗi và nguyên nhân để client hiển thị đúng tại field tương ứng:

```json
{
  "code": "3005",
  "message": "Một hoặc nhiều thuộc tính sản phẩm không hợp lệ",
  "data": [
    {
      "attributeDefinitionId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "attributeCode": "ram_capacity",
      "attributeName": "Dung lượng RAM",
      "rejectedValue": "32",
      "reason": "INACTIVE"
    }
  ]
}
```

`reason` có thể là `DUPLICATE`, `NOT_ALLOWED`, `DELETED`, `INACTIVE`, `INVALID_VALUE` hoặc `REQUIRED_MISSING`.

## Attribute Definition API

| Method | Endpoint | HTTP | Mô tả |
|---|---|---|---|
| `POST` | `/attribute-definitions` | `201` | Tạo định nghĩa thuộc tính |
| `GET` | `/attribute-definitions/{id}` | `200` | Lấy chi tiết |
| `GET` | `/attribute-definitions/search` | `200` | Tìm kiếm, lọc và phân trang |
| `GET` | `/attribute-definitions/options` | `200` | Lấy thuộc tính active, chưa bị xóa |
| `PUT` | `/attribute-definitions/{id}` | `200` | Cập nhật |
| `PATCH` | `/attribute-definitions/{id}/status?active=true` | `200` | Đổi trạng thái |
| `DELETE` | `/attribute-definitions/{id}` | `204` | Soft delete |

### Tạo định nghĩa thuộc tính

```http
POST /api/v1/admin/attribute-definitions
Content-Type: application/json
```

```json
{
  "name": "Dung lượng RAM",
  "code": "ram_capacity",
  "unit": "GB",
  "dataType": "NUMBER"
}
```

`name`, `code` và `dataType` là bắt buộc; `code` phải duy nhất. `dataType` nhận một trong các giá trị:

```text
TEXT, NUMBER, BOOLEAN, DATE, SELECT
```

### Tìm kiếm định nghĩa thuộc tính

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `keyword` | `string` | Tìm theo tên hoặc code |
| `active` | `boolean` | Lọc theo trạng thái |
| `dataType` | `enum` | Lọc theo kiểu dữ liệu |
| `page`, `size`, `sort` | pagination | Mặc định sort theo `createdAt,desc` và `id,desc` |

```http
GET /api/v1/admin/attribute-definitions/search?keyword=ram&active=true&dataType=NUMBER&page=1&size=15&sort=name,asc
```

Search và options luôn loại bản ghi đã soft delete. Options được sắp xếp theo tên tăng dần.

Payload cập nhật có thể chứa một hoặc nhiều field:

```json
{
  "name": "Dung lượng bộ nhớ RAM",
  "code": "memory_capacity",
  "unit": "GiB",
  "dataType": "NUMBER"
}
```

## Response

Response thành công:

```json
{
  "code": "200",
  "message": "Thành công",
  "data": {}
}
```

Response tạo mới:

```json
{
  "code": "201",
  "message": "Tạo mới thành công",
  "data": {}
}
```

`code` trong body là mã kết quả ứng dụng, độc lập với HTTP status.

Response phân trang:

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

Lỗi nghiệp vụ, validation, JSON không hợp lệ và vi phạm ràng buộc dữ liệu được chuyển thành `ApiResponse` với HTTP status tương ứng.

```json
{
  "code": "1001",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "data": [
    {
      "field": "name",
      "message": "Tên thuộc tính không được để trống"
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
| `2404` | `400 Bad Request` | Sản phẩm đã bị xóa hoặc đang `INACTIVE` khi gọi delete |
| `2601` | `404 Not Found` | Không tìm thấy định nghĩa thuộc tính |
| `2602` | `409 Conflict` | Mã định nghĩa thuộc tính đã tồn tại |
| `2603` | `400 Bad Request` | Định nghĩa thuộc tính chưa được kích hoạt |
| `2801` | `404 Not Found` | Thuộc tính chưa được gán cho danh mục |
| `2802` | `409 Conflict` | Thuộc tính đã được gán cho danh mục |
| `3001` | `400 Bad Request` | Thuộc tính không áp dụng cho category của product |
| `3002` | `400 Bad Request` | Thiếu thuộc tính bắt buộc |
| `3003` | `400 Bad Request` | Giá trị thuộc tính không đúng kiểu dữ liệu |
| `3004` | `400 Bad Request` | Thuộc tính sản phẩm bị trùng trong request |
| `3005` | `400 Bad Request` | Một hoặc nhiều thuộc tính sản phẩm không hợp lệ; `data` chứa chi tiết từng attribute |

## Ghi chú phát triển

- Hibernate đang dùng `ddl-auto: update`; nên chuyển sang Flyway hoặc Liquibase trước production.
- `open-in-view` đã tắt; quan hệ lazy cần được ánh xạ sang DTO trong transaction.
- Các module Identity, Order, Payment và một số tích hợp hạ tầng hiện mới là cấu trúc chuẩn bị.
