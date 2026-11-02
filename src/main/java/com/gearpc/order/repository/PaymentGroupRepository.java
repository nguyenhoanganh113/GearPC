package com.gearpc.order.repository;

import com.gearpc.order.domain.entity.PaymentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentGroupRepository extends JpaRepository<PaymentGroup, UUID> {
    boolean existsByCode(String code);
}
