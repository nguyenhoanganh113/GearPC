package com.gearpc.catalog.domain.entity;

import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attribute_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDefinition extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "code", length = 255, nullable = false, unique = true)
    private String code;

    @Column(name = "unit", length = 255)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", length = 50, nullable = false)
    private AttributeDataType dataType;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = false;

    @OneToMany(mappedBy = "attributeDefinition")
    @Builder.Default
    private List<CategoryAttribute> categoryAttributes = new ArrayList<>();

}
