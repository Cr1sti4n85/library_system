package com.soracel.library_system.payments;

import com.soracel.library_system.domain.*;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.PaymentMapper;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.PaymentDTO;
import com.soracel.library_system.payments.PaymentService;
import com.soracel.library_system.repository.FineRepository;
import com.soracel.library_system.repository.PaymentRepository;
import com.soracel.library_system.repository.SubscriptionRepository;
import com.soracel.library_system.service.SubscriptionService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final UserService userService;
    private final SubscriptionService  subscriptionService;
    private final SubscriptionRepository  subscriptionRepository;
    private final FineRepository fineRepository;

    @Override
    public void createPayment(User user, Subscription subscription, Fine fine, PaymentType paymentType,
                                 PaymentProvider provider, String customerId, BigDecimal amount, PaymentStatus status) {

        Payment payment;
        if (subscription == null) {
            payment = Payment.builder()
                    .user(user)
                    .fine(fine)
                    .paymentProvider(provider)
                    .customerId(customerId)
                    .paymentType(paymentType)
                    .status(status)
                    .amount(amount)
                    .build();
        } else {
            payment = Payment.builder()
                    .user(user)
                    .subscription(subscription)
                    .paymentProvider(provider)
                    .customerId(customerId)
                    .paymentType(paymentType)
                    .status(status)
                    .amount(subscription.getPrice())
                    .build();
        }

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
                if (paymentResult.getMetadata().containsKey("fineId")){
                    //update fine
                    Fine fine = fineRepository.findById(paymentResult.getMetadata().get("fineId"))
                            .orElseThrow(() -> new NotFoundException("Multa no encontrada"));
                    fine.setPaidAt(LocalDateTime.now());
                    fine.setStatus(FineStatus.PAID);
                    fine.setPaymentIntentId(paymentResult.getPaymentIntentId());
                    fineRepository.save(fine);
                } else {
                    userService.updateUserCustomerId(
                            paymentResult.getMetadata().get("userId"),
                            paymentResult.getCustomerId());

                    subscriptionService.activateSubscription(paymentResult);
                }

            }

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.SUCCEEDED)){

                if (paymentResult.getPaymentIntentId() != null){
                    Fine fine = fineRepository.findByPaymentIntentId(paymentResult.getPaymentIntentId())
                            .orElseThrow(() -> new NotFoundException("Multa no encontrada por payment id"));

                    createPayment(
                            fine.getUser(),
                            null,
                            fine,
                            PaymentType.FINE,
                            PaymentProvider.STRIPE,
                            null,
                            fine.getAmount(),
                            PaymentStatus.SUCCEEDED
                    );

                } else {
                    User user = userService.findByCustomerId(paymentResult.getCustomerId());
                    Subscription activeSub = subscriptionService
                            .findByCustomerIdAndStatus(
                                    paymentResult.getCustomerId(),
                                    SubscriptionStatus.ACTIVE);
                    //TODO save invoice id in payment. will be used as idempotency key

                    createPayment(
                            user,
                            activeSub,
                            null,
                            PaymentType.SUBSCRIPTION,
                            PaymentProvider.STRIPE,
                            paymentResult.getCustomerId(),
                            activeSub.getPrice(),
                            PaymentStatus.SUCCEEDED
                    );
                }

            }
            if (paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)){
                if (paymentResult.getPaymentIntentId() != null){
                    Fine fine = fineRepository.findByPaymentIntentId(paymentResult.getPaymentIntentId())
                            .orElseThrow(() -> new NotFoundException("Multa no encontrada"));

                    createPayment(
                            fine.getUser(),
                            null,
                            fine,
                            PaymentType.FINE,
                            PaymentProvider.STRIPE,
                            null,
                            fine.getAmount(),
                            PaymentStatus.FAILED
                    );
                } else {
                    User foundUser = userService.findByCustomerId(paymentResult.getCustomerId());
                    //DEACTIVATE SUBSCRIPTION
                    Subscription sub = subscriptionService
                            .findByCustomerIdAndStatus(paymentResult.getCustomerId(), SubscriptionStatus.ACTIVE);
                    sub.setStatus(SubscriptionStatus.PAYMENT_FAILED);
                    sub.setIsActive(false);
                    Subscription updatedSub = subscriptionRepository.save(sub);

                    //TODO save invoice id in payment. will be used as idempotency key
                    //CREATE FAILED SUBSCRIPTION PAYMENT
                    createPayment(
                            foundUser,
                            updatedSub,
                            null,
                            PaymentType.SUBSCRIPTION,
                            PaymentProvider.STRIPE,
                            paymentResult.getCustomerId(),
                            updatedSub.getPrice(),
                            PaymentStatus.FAILED
                    );
                }
                //TODO notify by email

            }
        });
    }
}
