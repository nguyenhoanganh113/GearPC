package com.gearpc.order.repository;

import com.gearpc.order.domain.entity.PaymentGroupMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentGroupMethodRepository extends JpaRepository<PaymentGroupMethod, UUID> {
    boolean existsByGroupIdAndMethodId(UUID groupId, UUID methodId);
}
