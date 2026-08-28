package com.gearpc.catalog.domain.entity;

import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
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

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "price", precision = 15, scale = 2)
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
