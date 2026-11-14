package com.gearpc.order.controller;

import com.gearpc.common.dto.ApiResponse;
import com.gearpc.order.application.dto.request.AssignPaymentMethodRequest;
import com.gearpc.order.application.dto.response.AssignPaymentMethodResponse;
import com.gearpc.order.application.service.PaymentGroupMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payment-groups")
@PreAuthorize("denyAll()")
public class PaymentGroupMethodController {

    private final PaymentGroupMethodService paymentGroupMethodService;

    @PostMapping("/{groupId}/methods")
    @PreAuthorize("hasAuthority('PERM_ASSIGN_PAYMENT_GROUP')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssignPaymentMethodResponse> assignPaymentMethod(
            @PathVariable UUID groupId,
            @Valid @RequestBody AssignPaymentMethodRequest request
    ) {
        return ApiResponse.created(paymentGroupMethodService.assignPaymentMethod(groupId, request));
    }
}
