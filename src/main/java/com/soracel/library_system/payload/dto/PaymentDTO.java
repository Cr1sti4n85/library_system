package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;

    private Long userId;

    private String fullName;

    private String email;

    private Long subscriptionId;

    private String planName;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    private PaymentType paymentType;

    private PaymentStatus status;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime paidAt;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
