package com.osoki.paymentsystem.payment.service;

import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.payment.entity.PaymentStatus;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(String idempotencyKey,
                                  PaymentRequest request);

    PaymentResponse getPaymentById(UUID id);

    PaymentResponse markAsCompleted(UUID id);

    PaymentResponse markAsFailed(UUID id);

    PaymentResponse updateStatus(UUID id, PaymentStatus newStatus);
}
