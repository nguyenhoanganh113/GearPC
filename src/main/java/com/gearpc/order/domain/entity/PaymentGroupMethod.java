package com.gearpc.order.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "group_payment_method",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_payment_method",
                columnNames = {"group_id", "method_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGroupMethod extends BaseEntity {

    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "method_id")
    private UUID methodId;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "max_discount_amount", precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "extra_fee_percent")
    private BigDecimal extraFeePercent;

    @Column(name = "extra_fee_amount")
    private BigDecimal extraFeeAmount;

    @Column(name = "description")
    private String description;

    @Version
    private Long version;

    @Column(name = "sort_order")
    private Integer sortOrder;

}
