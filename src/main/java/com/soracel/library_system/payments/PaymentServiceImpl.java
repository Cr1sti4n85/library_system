package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.mapper.PaymentMapper;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.PaymentDTO;
import com.soracel.library_system.payments.PaymentService;
import com.soracel.library_system.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .customerId(customerId)
                .paymentType(paymentType)
                .status(status)
                .amount(subscription.getPrice())
                .build();
        paymentRepository.save(payment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(PaymentMapper::toDTO);
    }
}
