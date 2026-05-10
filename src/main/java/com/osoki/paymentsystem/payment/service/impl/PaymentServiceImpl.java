package com.osoki.paymentsystem.payment.service.impl;

import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.payment.entity.Payment;
import com.osoki.paymentsystem.payment.repository.PaymentRepository;
import com.osoki.paymentsystem.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        //dto to entity
        Payment payment = Payment.builder()
                .userId(request.userId())
                .amount(request.amount())
                .build();
        //DB save
        Payment savedPayment = paymentRepository.save(payment);

        // entity to response
        return new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getUserId(),
                savedPayment.getAmount(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt()
        );
    }
}
