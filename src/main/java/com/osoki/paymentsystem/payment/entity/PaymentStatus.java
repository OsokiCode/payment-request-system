package com.osoki.paymentsystem.payment.entity;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED;

    public boolean canTransitionTo(PaymentStatus next) {
        return switch (this) {
            case PENDING -> next == COMPLETED || next == FAILED;
            case COMPLETED, FAILED -> false;
        };
    }
}
