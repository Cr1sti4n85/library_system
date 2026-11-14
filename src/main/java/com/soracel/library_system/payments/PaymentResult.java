package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PaymentResult {
    private PaymentStatus paymentStatus;
}
