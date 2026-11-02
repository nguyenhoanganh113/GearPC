package com.gearpc.order.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.order.application.dto.request.AssignPaymentMethodRequest;
import com.gearpc.order.application.dto.response.AssignPaymentMethodResponse;
import com.gearpc.order.application.service.PaymentGroupMethodService;
import com.gearpc.order.domain.entity.PaymentGroupMethod;
import com.gearpc.order.repository.PaymentGroupRepository;
import com.gearpc.order.repository.PaymentGroupMethodRepository;
import com.gearpc.order.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentGroupMethodServiceImpl implements PaymentGroupMethodService {

    private final PaymentGroupRepository paymentGroupRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentGroupMethodRepository paymentGroupMethodRepository;

    @Override
    @Transactional
    public AssignPaymentMethodResponse assignPaymentMethod(UUID groupId, AssignPaymentMethodRequest request) {
        if (!paymentGroupRepository.existsByIdAndDeletedAtIsNull(groupId)) {
            throw new AppException(ErrorCode.PAYMENT_GROUP_NOT_FOUND);
        }
        if (!paymentMethodRepository.existsByIdAndDeletedAtIsNull(request.methodId())) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_FOUND);
        }
        // Include soft-deleted assignments: the database unique key also reserves these pairs.
        if (paymentGroupMethodRepository.existsByGroupIdAndMethodId(groupId, request.methodId())) {
            throw new AppException(ErrorCode.PAYMENT_GROUP_METHOD_EXISTS);
        }
        PaymentGroupMethod assignment = PaymentGroupMethod.builder()
                .groupId(groupId).methodId(request.methodId())
                .discountPercent(request.discountPercent()).maxDiscountAmount(request.maxDiscountAmount())
                .minAmount(request.minAmount()).extraFeePercent(request.extraFeePercent())
                .extraFeeAmount(request.extraFeeAmount()).description(request.description())
                .sortOrder(request.sortOrder()).build();
        try {
            assignment = paymentGroupMethodRepository.saveAndFlush(assignment);
        } catch (DataIntegrityViolationException ex) {
            // Concurrent requests can pass the existence check together.
            for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
                if (cause instanceof ConstraintViolationException violation
                        && "uk_group_payment_method".equals(violation.getConstraintName())) {
                    throw new AppException(ErrorCode.PAYMENT_GROUP_METHOD_EXISTS);
                }
            }
            throw ex;
        }
        return new AssignPaymentMethodResponse(assignment.getId(), assignment.getGroupId(),
                assignment.getMethodId(), assignment.getDiscountPercent(), assignment.getMaxDiscountAmount(),
                assignment.getMinAmount(), assignment.getExtraFeePercent(), assignment.getExtraFeeAmount(),
                assignment.getDescription(), assignment.getSortOrder(), assignment.getVersion(), assignment.getCreatedAt());
    }
}
