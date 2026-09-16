package com.gearpc.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

}
