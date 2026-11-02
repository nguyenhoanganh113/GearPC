package com.gearpc.order.repository;

import com.gearpc.order.domain.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    boolean existsByIdAndDeletedAtIsNull(UUID id);

    boolean existsByCode(String code);
}
