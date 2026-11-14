package com.gearpc.order.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import com.gearpc.order.domain.valueobject.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "payment_method_id", nullable = false)
    private UUID paymentMethodId;

    @Column(name = "payment_code", nullable = false, unique = true, length = 100)
    private String paymentCode;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;


}
