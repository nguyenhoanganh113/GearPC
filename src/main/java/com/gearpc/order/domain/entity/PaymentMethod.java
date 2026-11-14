package com.gearpc.order.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "payment_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    private Integer status;

    private String icon;

    private Integer type;

    @Builder.Default
    @Column(name = "can_view_anonymous", nullable = false)
    private Boolean canViewAnonymous = false;

}
