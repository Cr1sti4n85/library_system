package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Payment;
import com.soracel.library_system.payload.dto.PaymentDTO;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentDTO.PaymentDTOBuilder builder = PaymentDTO.builder()
                .id(payment.getId())
                .currency(payment.getCurrency())
                .paidAt(payment.getPaidAt());

        if (payment.getUser() != null) {
            builder.userId(payment.getUser().getId())
                    .fullName(payment.getUser().getFullName())
                    .email(payment.getUser().getEmail());
        }

        if (payment.getSubscription() != null) {
            builder.subscriptionId(payment.getSubscription().getId())
                    .planName(payment.getSubscription().getPlanName());
        }
        return builder.build();
    }

}
