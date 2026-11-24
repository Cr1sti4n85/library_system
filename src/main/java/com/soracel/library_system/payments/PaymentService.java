package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentService {
    void createPayment(User user,
                          Subscription subscription,
                          Fine fine,
                          PaymentType paymentType,
                          PaymentProvider provider,
                          String customerId,
                          BigDecimal amount,
                          PaymentStatus status,
                          LocalDateTime paidAt
                          );

    void handleWebhookEvent(WebhookRequest request);
    
    Page<PaymentDTO> getAllPayments(Pageable pageable);
}
