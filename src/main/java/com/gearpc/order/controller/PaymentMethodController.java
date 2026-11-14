package com.gearpc.order.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.order.application.dto.request.CreatePaymentMethodRequest;
import com.gearpc.order.application.dto.response.CreatePaymentMethodResponse;
import com.gearpc.order.application.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payment-methods")
@PreAuthorize("denyAll()")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_CREATE_PAYMENT_METHOD')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatePaymentMethodResponse> createPaymentMethod(
            @Valid @RequestBody CreatePaymentMethodRequest request
    ) {
        return ApiResponse.created(paymentMethodService.createPaymentMethod(request));
    }
}
