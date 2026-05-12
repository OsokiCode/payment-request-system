package com.osoki.paymentsystem.common.exception;

public class PaymentAlreadyProcessingException extends RuntimeException {
    public PaymentAlreadyProcessingException(String idempotencyKey) {
        super("Payment already processing for key: " + idempotencyKey);
    }
}