package com.osoki.paymentsystem.payment.service;

import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(String idempotencyKey,
                                  PaymentRequest request);
}
