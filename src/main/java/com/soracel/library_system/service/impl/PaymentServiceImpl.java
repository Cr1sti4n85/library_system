package com.soracel.library_system.service.impl;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payments.PaymentService;
import com.soracel.library_system.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;


    @Override
    public void createSubscriptionPayment(User user, Subscription subscription, PaymentType paymentType,
                                 PaymentProvider provider, String customerId, BigDecimal amount, PaymentStatus status) {

        Payment payment = Payment.builder()
                .user(user)
                .subscription(subscription)
                .paymentProvider(provider)
                .stripeCustomerId(customerId)
                .paymentType(paymentType)
                .status(status)
                .amount(subscription.getPrice())
                .build();
        paymentRepository.save(payment);
    }
}
