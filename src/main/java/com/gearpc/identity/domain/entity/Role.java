package com.gearpc.identity.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleType name;

    private String description;

}
