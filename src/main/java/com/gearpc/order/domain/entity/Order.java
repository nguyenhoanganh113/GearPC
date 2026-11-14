package com.gearpc.order.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "address_id")
    private UUID addressId;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    @Column(name = "order_status")
    private Integer orderStatus;

}
