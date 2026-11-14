package com.gearpc.order.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.order.application.dto.request.CreatePaymentGroupRequest;
import com.gearpc.order.application.dto.response.CreatePaymentGroupResponse;
import com.gearpc.order.application.service.PaymentGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payment-groups")
@PreAuthorize("denyAll()")
public class PaymentGroupController {

    private final PaymentGroupService paymentGroupService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_CREATE_PAYMENT_GROUP')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatePaymentGroupResponse> createPaymentGroup(
            @Valid @RequestBody CreatePaymentGroupRequest request
    ) {
        return ApiResponse.created(paymentGroupService.createPaymentGroup(request));
    }
}
