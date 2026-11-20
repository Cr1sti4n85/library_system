package com.soracel.library_system.service.impl;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.domain.SubscriptionStatus;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.SubscriptionException;
import com.soracel.library_system.mapper.SubscriptionMapper;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.payments.*;
import com.soracel.library_system.repository.SubscriptionRepository;
import com.soracel.library_system.service.SubscriptionPlanService;
import com.soracel.library_system.service.SubscriptionService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanService  subscriptionPlanService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final PaymentGateway paymentGateway;


    @Override
    public CheckoutSession subscribe(SubscriptionDTO subscriptionDTO) {

        User currentUser = userService.getCurrentUserEntity();
        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(subscriptionDTO.getPlanId());

        Subscription subscription = SubscriptionMapper
                .toEntity(subscriptionDTO, currentUser, plan);

        subscription.initializeSubscriptionFromPlan();
        Subscription newSub = subscriptionRepository.save(subscription);


        return paymentGateway.createCheckoutSession(currentUser.getId(), newSub.getId(), plan.getStripePriceId());

    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) {
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDate.now())
                .orElseThrow(() -> new NotFoundException("El usuario actual no tiene subscripciones activas"));
        return SubscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("ID de subscripción incorrecto"));

        if (!subscription.getIsActive()){
            throw new SubscriptionException("La subscripción ya está inactiva");
        }

        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason);
        Subscription updatedSubscription =  subscriptionRepository.save(subscription);

        return SubscriptionMapper.toDTO(updatedSubscription);
    }

    @Override
    public void activateSubscription(PaymentResult paymentResult) {

        Subscription sub = subscriptionRepository
                .findByIdAndStatus(paymentResult.getMetadata().get("subscriptionId"), SubscriptionStatus.PENDING)
                .orElseThrow(() -> new NotFoundException("No se encuentra la suscripción"));
        sub.setCustomerId(paymentResult.getCustomerId());
        sub.setIsActive(true);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(sub);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return SubscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    public void deactivateExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                                                    .findExpiredActiveSubscriptions(LocalDate.now());
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhook(request).ifPresent(paymentResult -> {

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.COMPLETED)) {

                userService.updateUserCustomerId(
                        paymentResult.getMetadata().get("userId"),
                        paymentResult.getCustomerId());

                activateSubscription(paymentResult);

            }

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.SUCCEEDED)){

                User user = userService.findByCustomerId(paymentResult.getCustomerId());
                Subscription activeSub = subscriptionRepository.findByCustomerIdAndStatus(paymentResult.getCustomerId(),
                        SubscriptionStatus.ACTIVE).orElseThrow(() ->
                        new NotFoundException("El usuario actual no tiene subscripciones activas"));

                //TODO save invoice id in payment. will be used as idempotency key

                paymentService.createSubscriptionPayment(
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
                Subscription sub = subscriptionRepository
                        .findByCustomerIdAndStatus(paymentResult.getCustomerId(), SubscriptionStatus.ACTIVE)
                        .orElseThrow(() -> new NotFoundException("Usuario no tiene suscripciones activas"));
                sub.setStatus(SubscriptionStatus.PAYMENT_FAILED);
                sub.setIsActive(false);
                Subscription updatedSub = subscriptionRepository.save(sub);

                //TODO save invoice id in payment. will be used as idempotency key
                //CREATE FAILED SUBSCRIPTION PAYMENT
                paymentService.createSubscriptionPayment(
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
