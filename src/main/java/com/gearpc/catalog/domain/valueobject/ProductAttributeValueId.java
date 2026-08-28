package com.gearpc.catalog.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductAttributeValueId implements Serializable {

    @Column(name = "product_id", length = 36, nullable = false)
    private UUID productId;

    @Column(name = "attribute_definition_id", length = 36, nullable = false)
    private UUID attributeDefinitionId;

}
