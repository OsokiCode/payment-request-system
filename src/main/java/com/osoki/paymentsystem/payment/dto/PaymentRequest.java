package com.osoki.paymentsystem.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
public record PaymentRequest(
        @NotNull
        Long userId,
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}
