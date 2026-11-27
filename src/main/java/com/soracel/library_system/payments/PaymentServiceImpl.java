package com.soracel.library_system.payments;

import com.soracel.library_system.domain.*;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.PaymentMapper;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.PaymentDTO;
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
    public void createPayment(User user, Subscription subscription,
                              Fine fine, PaymentType paymentType,
                              PaymentProvider provider, String customerId,
                              String invoiceId, BigDecimal amount, PaymentStatus status,
                              LocalDateTime paidAt

    ) {

        Payment.PaymentBuilder builder = Payment.builder()
                .user(user)
                .paymentProvider(provider)
                .customerId(customerId)
                .paymentType(paymentType)
                .status(status)
                .amount(amount)
                .paidAt(paidAt);

        if (subscription == null) {
            builder.fine(fine);
        } else {
            builder.subscription(subscription)
                    .invoiceId(invoiceId);
        }

        paymentRepository.save(builder.build());
    }


    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(PaymentMapper::toDTO);
    }

    @Override
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhook(request).ifPresent(paymentResult -> {

            if (PaymentStatus.COMPLETED == paymentResult.getPaymentStatus()) {

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

            if (PaymentStatus.SUCCEEDED ==  paymentResult.getPaymentStatus()) {
                //in subscriptions it's not null
                if (paymentResult.getCustomerId() == null){
                    Fine fine = fineRepository.findById(paymentResult.getMetadata().get("fineId"))
                            .orElseThrow(() -> new NotFoundException("Multa no encontrada"));

                    createPayment(
                            fine.getUser(),
                            null,
                            fine,
                            PaymentType.FINE,
                            PaymentProvider.STRIPE,
                            null,
                            null,
                            fine.getAmount(),
                            PaymentStatus.SUCCEEDED,
                            paymentResult.getPaidAt());

                }

            } if (PaymentStatus.SUBSCRIPTION_SUCCEEDED ==  paymentResult.getPaymentStatus()) {
                User user = userService.getUserById(paymentResult.getMetadata().get("userId"));
                Subscription subscription = subscriptionRepository.findById(
                        paymentResult.getMetadata().get("subscriptionId"))
                        .orElseThrow(() -> new NotFoundException("Multa no encontrada"));

                if (SubscriptionStatus.PAYMENT_FAILED == subscription.getStatus()){
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                    subscription.setIsActive(true);
                }
                subscription.setStartDate(paymentResult.getSubscriptionDates().get("startDate"));
                subscription.setEndDate(paymentResult.getSubscriptionDates().get("endDate"));
                subscription = subscriptionRepository.save(subscription);

                createPayment(
                        user,
                        subscription,
                        null,
                        PaymentType.SUBSCRIPTION,
                        PaymentProvider.STRIPE,
                        paymentResult.getCustomerId(),
                        paymentResult.getInvoiceId(),
                        subscription.getPrice(),
                        PaymentStatus.SUBSCRIPTION_SUCCEEDED,
                        paymentResult.getPaidAt());

            }
            if (PaymentStatus.FAILED ==  paymentResult.getPaymentStatus()) {
                if (paymentResult.getCustomerId() == null){
                    Fine fine = fineRepository.findById(paymentResult.getMetadata().get("fineId"))
                            .orElseThrow(() -> new NotFoundException("Multa no encontrada"));

                    createPayment(
                            fine.getUser(),
                            null,
                            fine,
                            PaymentType.FINE,
                            PaymentProvider.STRIPE,
                            null,
                            null,
                            fine.getAmount(),
                            PaymentStatus.FAILED,
                            paymentResult.getPaidAt()
                    );
                    //TODO send email to notify client.
                }
                //TODO send notification when payment failed comes from subscription?
            }
            if (PaymentStatus.SUBSCRIPTION_FAILED ==  paymentResult.getPaymentStatus()) {
                User foundUser = userService.getUserById(paymentResult.getMetadata().get("userId"));
                Subscription subscription = subscriptionRepository.findById(
                                paymentResult.getMetadata().get("subscriptionId"))
                        .orElseThrow(() -> new NotFoundException("Multa no encontrada"));
                subscription.setStatus(SubscriptionStatus.PAYMENT_FAILED);
                subscription.setIsActive(false);
                Subscription updatedSub = subscriptionRepository.save(subscription);

                //CREATE FAILED SUBSCRIPTION PAYMENT
                createPayment(
                        foundUser,
                        updatedSub,
                        null,
                        PaymentType.SUBSCRIPTION,
                        PaymentProvider.STRIPE,
                        paymentResult.getCustomerId(),
                        paymentResult.getInvoiceId(),
                        updatedSub.getPrice(),
                        PaymentStatus.SUBSCRIPTION_FAILED,
                        paymentResult.getPaidAt()
                    );
                //TODO notify by email, send link to change account details

            }
        });
    }
}
