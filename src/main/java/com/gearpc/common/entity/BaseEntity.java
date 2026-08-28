package com.gearpc.common.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    protected void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch(); // UUID v7
        }
    }

}
