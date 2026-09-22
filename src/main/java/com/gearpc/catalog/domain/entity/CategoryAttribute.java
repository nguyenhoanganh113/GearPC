package com.gearpc.catalog.domain.entity;

import com.gearpc.catalog.domain.valueobject.CategoryAttributeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_attributes")
@Getter
@Setter
@NoArgsConstructor
public class CategoryAttribute {

    @EmbeddedId
    private CategoryAttributeId id;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @MapsId("attributeDefinitionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_definition_id", nullable = false)
    private AttributeDefinition attributeDefinition;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    public CategoryAttribute(
            Category category,
            AttributeDefinition attributeDefinition,
            boolean required
    ) {
        this.category = category;
        this.attributeDefinition = attributeDefinition;
        this.required = required;
        this.id = new CategoryAttributeId(category.getId(), attributeDefinition.getId());
    }
}
