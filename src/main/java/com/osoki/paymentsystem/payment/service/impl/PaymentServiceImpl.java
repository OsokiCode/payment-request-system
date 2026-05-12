package com.osoki.paymentsystem.payment.service.impl;

import com.osoki.paymentsystem.common.exception.PaymentAlreadyProcessingException;
import com.osoki.paymentsystem.common.exception.PaymentNotFoundException;
import com.osoki.paymentsystem.common.exception.PaymentValidationException;
import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.payment.entity.Payment;
import com.osoki.paymentsystem.payment.entity.PaymentStatus;
import com.osoki.paymentsystem.payment.repository.PaymentRepository;
import com.osoki.paymentsystem.payment.service.PaymentService;
import com.osoki.paymentsystem.redis.service.IdempotencyService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;


    @Override
    @Transactional
    public PaymentResponse createPayment(String idempotencyKey, PaymentRequest request) {

        Optional<PaymentResponse> cachedResponse =
                idempotencyService.getCachedResponse(idempotencyKey);

        if (cachedResponse.isPresent()){
            return cachedResponse.get();
        }

        boolean locked =
                idempotencyService.acquireLock(idempotencyKey);

        if (!locked) {
            throw new PaymentAlreadyProcessingException(idempotencyKey);
        }
        try {

            //re-check for cache
            Optional<PaymentResponse> cachedAfterLock =
                    idempotencyService.getCachedResponse(idempotencyKey);

            if (cachedAfterLock.isPresent()) {
                return cachedAfterLock.get();
            }

            //dto to entity
            Payment payment = Payment.builder()
                    .userId(request.userId())
                    .amount(request.amount())
                    .build();
            //DB save
            Payment savedPayment = paymentRepository.save(payment);

            // entity to response
            PaymentResponse response =
                    new PaymentResponse(
                        savedPayment.getId(),
                        savedPayment.getUserId(),
                        savedPayment.getAmount(),
                        savedPayment.getStatus(),
                        savedPayment.getCreatedAt(),
                        savedPayment.getUpdatedAt()
                    );

            // write cache

            idempotencyService.cacheResponse(idempotencyKey,response);

            return response;
        }finally {
            idempotencyService.releaseLock(idempotencyKey);
        }
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found"));

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse markAsCompleted(UUID id) {
        return updateStatus(id, PaymentStatus.COMPLETED);
    }

    @Override
    public PaymentResponse markAsFailed(UUID id) {
        return updateStatus(id, PaymentStatus.FAILED);
    }

    @Override
    @Transactional
    public PaymentResponse updateStatus(UUID id, PaymentStatus newStatus) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found"));

        validateTransition(payment.getStatus(), newStatus);

        payment.setStatus(newStatus);

        Payment saved = paymentRepository.save(payment);

        return mapToResponse(saved);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    private void validateTransition(PaymentStatus current, PaymentStatus next) {

        if (!current.canTransitionTo(next)) {
            throw new PaymentValidationException("Invalid status transition");
        }
    }
}
