package com.osoki.paymentsystem.payment.controller;

import com.osoki.paymentsystem.common.response.ApiResponse;
import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestHeader("Idempotency-Key")
            @NotBlank(message = "Idempotency-Key is required")
            String idempotencyKey,

            @Valid
            @RequestBody
            PaymentRequest request
    ) {
        PaymentResponse response =
                paymentService.createPayment(
                        idempotencyKey,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        paymentService.getPaymentById(id)
                )
        );
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<PaymentResponse>> completePayment(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        paymentService.markAsCompleted(id)
                )
        );
    }
    @PatchMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<PaymentResponse>> failPayment(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        paymentService.markAsFailed(id)
                )
        );
    }
}
