package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class PaymentResult {
    @NotNull
    private PaymentStatus paymentStatus;
    private String customerId;
    private Map<String, Long> metadata;
    private String paymentIntentId;
}
