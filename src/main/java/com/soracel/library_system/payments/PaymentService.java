package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;

import java.math.BigDecimal;

public interface PaymentService {
    void createSubscriptionPayment(User user,
                          Subscription subscription,
                          PaymentType paymentType,
                          PaymentProvider provider,
                          String customerId,
                          BigDecimal amount,
                          PaymentStatus status);
}
