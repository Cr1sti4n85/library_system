package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.domain.SubscriptionStatus;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.PaymentMapper;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.PaymentDTO;
import com.soracel.library_system.payments.PaymentService;
import com.soracel.library_system.repository.PaymentRepository;
import com.soracel.library_system.repository.SubscriptionRepository;
import com.soracel.library_system.service.SubscriptionService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final UserService userService;
    private final SubscriptionService  subscriptionService;
    private final SubscriptionRepository  subscriptionRepository;

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

    @Override
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhook(request).ifPresent(paymentResult -> {

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.COMPLETED)) {

                //todo: check if it's fine or subscription payment

                userService.updateUserCustomerId(
                        paymentResult.getMetadata().get("userId"),
                        paymentResult.getCustomerId());

                subscriptionService.activateSubscription(paymentResult);

            }

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.SUCCEEDED)){

                User user = userService.findByCustomerId(paymentResult.getCustomerId());
                Subscription activeSub = subscriptionService.findByCustomerIdAndStatus(paymentResult.getCustomerId(),
                        SubscriptionStatus.ACTIVE);

                //TODO save invoice id in payment. will be used as idempotency key

                createSubscriptionPayment(
                        user,
                        activeSub,
                        PaymentType.SUBSCRIPTION,
                        PaymentProvider.STRIPE,
                        paymentResult.getCustomerId(),
                        activeSub.getPrice(),
                        PaymentStatus.SUCCEEDED
                );

            }
            if (paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)){
                User foundUser = userService.findByCustomerId(paymentResult.getCustomerId());
                //DEACTIVATE SUBSCRIPTION
                Subscription sub = subscriptionService
                        .findByCustomerIdAndStatus(paymentResult.getCustomerId(), SubscriptionStatus.ACTIVE);
                sub.setStatus(SubscriptionStatus.PAYMENT_FAILED);
                sub.setIsActive(false);
                Subscription updatedSub = subscriptionRepository.save(sub);

                //TODO save invoice id in payment. will be used as idempotency key
                //CREATE FAILED SUBSCRIPTION PAYMENT
                createSubscriptionPayment(
                        foundUser,
                        updatedSub,
                        PaymentType.SUBSCRIPTION,
                        PaymentProvider.STRIPE,
                        paymentResult.getCustomerId(),
                        updatedSub.getPrice(),
                        PaymentStatus.FAILED
                );


                //TODO notify by email

            }
        });
    }
}
