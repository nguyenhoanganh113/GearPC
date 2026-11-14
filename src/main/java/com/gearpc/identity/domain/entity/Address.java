package com.gearpc.identity.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    private Boolean isDefault;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    private String streetDetail;

    @Column(nullable = false)
    private String ward;

    @Column(name = "user_id")
    private UUID userId;

}
