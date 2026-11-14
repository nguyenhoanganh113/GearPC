package com.gearpc.order.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.order.application.dto.request.CreatePaymentGroupRequest;
import com.gearpc.order.application.dto.response.CreatePaymentGroupResponse;
import com.gearpc.order.application.service.PaymentGroupService;
import com.gearpc.order.domain.entity.PaymentGroup;
import com.gearpc.order.repository.PaymentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentGroupServiceImpl implements PaymentGroupService {

    private final PaymentGroupRepository paymentGroupRepository;

    @Override
    @Transactional
    public CreatePaymentGroupResponse createPaymentGroup(CreatePaymentGroupRequest request) {
        String code = request.code().strip();
        if (paymentGroupRepository.existsByCode(code)) {
            throw new AppException(ErrorCode.PAYMENT_GROUP_EXISTS);
        }

        PaymentGroup group = PaymentGroup.builder()
                .name(request.name().strip())
                .code(code)
                .sortOrder(request.sortOrder())
                .status(request.status())
                .build();
        group = paymentGroupRepository.saveAndFlush(group);

        return new CreatePaymentGroupResponse(group.getId(), group.getName(), group.getCode(),
                group.getSortOrder(), group.getStatus(), group.getCreatedAt());
    }
}
