package com.gearpc.catalog.domain.entity;

import com.gearpc.catalog.domain.valueobject.ProductAttributeValueId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute_values")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ProductAttributeValue {

    @EmbeddedId
    private ProductAttributeValueId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("attributeDefinitionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_definition_id", nullable = false)
    private AttributeDefinition attributeDefinition;

    @Column(name = "value", length = 36, nullable = false)
    private String value;

    public ProductAttributeValue(Product product, AttributeDefinition attributeDefinition, String value) {
        this.product = product;
        this.attributeDefinition = attributeDefinition;
        this.value = value;
        this.id = new ProductAttributeValueId(product.getId(), attributeDefinition.getId());
    }

}
