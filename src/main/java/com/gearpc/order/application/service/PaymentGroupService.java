package com.gearpc.order.application.service;

import com.gearpc.order.application.dto.request.CreatePaymentGroupRequest;
import com.gearpc.order.application.dto.response.CreatePaymentGroupResponse;

public interface PaymentGroupService {
    CreatePaymentGroupResponse createPaymentGroup(CreatePaymentGroupRequest request);
}
