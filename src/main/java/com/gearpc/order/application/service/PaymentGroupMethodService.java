package com.gearpc.order.application.service;

import com.gearpc.order.application.dto.request.AssignPaymentMethodRequest;
import com.gearpc.order.application.dto.response.AssignPaymentMethodResponse;

import java.util.UUID;

public interface PaymentGroupMethodService {
    AssignPaymentMethodResponse assignPaymentMethod(UUID groupId, AssignPaymentMethodRequest request);
}
