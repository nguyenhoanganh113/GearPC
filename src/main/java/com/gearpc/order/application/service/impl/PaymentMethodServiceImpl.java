package com.gearpc.order.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.order.application.dto.request.CreatePaymentMethodRequest;
import com.gearpc.order.application.dto.response.CreatePaymentMethodResponse;
import com.gearpc.order.application.service.PaymentMethodService;
import com.gearpc.order.domain.entity.PaymentMethod;
import com.gearpc.order.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    @Transactional
    public CreatePaymentMethodResponse createPaymentMethod(CreatePaymentMethodRequest request) {
        String code = request.code().strip();
        if (paymentMethodRepository.existsByCode(code)) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_EXISTS);
        }

        PaymentMethod method = PaymentMethod.builder()
                .name(request.name().strip())
                .code(code)
                .status(request.status())
                .icon(request.icon())
                .type(request.type())
                .canViewAnonymous(Boolean.TRUE.equals(request.canViewAnonymous()))
                .build();
        method = paymentMethodRepository.saveAndFlush(method);

        return new CreatePaymentMethodResponse(method.getId(), method.getName(), method.getCode(),
                method.getStatus(), method.getIcon(), method.getType(),
                method.getCanViewAnonymous(), method.getCreatedAt());
    }
}
