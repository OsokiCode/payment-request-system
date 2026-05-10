package com.osoki.paymentsystem.payment.controller;

import com.osoki.paymentsystem.common.response.ApiResponse;
import com.osoki.paymentsystem.payment.dto.PaymentRequest;
import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(paymentService.createPayment(request)));
    }
}
