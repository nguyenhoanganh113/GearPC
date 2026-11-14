package com.gearpc.order.application.service;

import com.gearpc.order.application.dto.request.CreatePaymentMethodRequest;
import com.gearpc.order.application.dto.response.CreatePaymentMethodResponse;

public interface PaymentMethodService {
    CreatePaymentMethodResponse createPaymentMethod(CreatePaymentMethodRequest request);
}
