package com.gearpc.catalog.domain.entity;

import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        indexes = {
                // 1. Luồng lọc theo Status + Sort Giá + Tie-breaker
                @Index(name = "idx_products_status_price_id", columnList = "product_status, price DESC, id DESC"),

                // 2. Luồng XEM TẤT CẢ (không filter status) + Sort Giá + Tie-breaker
                @Index(name = "idx_products_price_id", columnList = "price DESC, id DESC"),

                // 3. Luồng lọc theo Status + Sort Ngày tạo (Mới nhất lên đầu) + Tie-breaker
                @Index(name = "idx_products_status_created_at_id", columnList = "product_status, created_at DESC, id DESC"),

                // 4. Luồng XEM TẤT CẢ + Sort Ngày tạo (Mới nhất)
                @Index(name = "idx_products_created_at_id", columnList = "created_at DESC, id DESC"),

                // 5. Index đơn cho các Khóa ngoại (dùng khi Admin lọc theo Danh mục / Thương hiệu)
                @Index(name = "idx_products_category_id", columnList = "category_id"),
                @Index(name = "idx_products_brand_id", columnList = "brand_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(name = "images", columnDefinition = "jsonb")
    @Column(name = "images")
    private String images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private List<ProductAttributeValue> attributeValues = new ArrayList<>();

}
