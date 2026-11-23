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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanService  subscriptionPlanService;
    private final UserService userService;
    private final PaymentGateway paymentGateway;


    @Override
    public CheckoutSession subscribe(SubscriptionDTO subscriptionDTO) {

        User currentUser = userService.getCurrentUserEntity();
        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(subscriptionDTO.getPlanId());

        Optional<Subscription> existingSub = subscriptionRepository.findByUserIdAndPlanIdAndStatus(
                currentUser.getId(),
                plan.getId(),
                SubscriptionStatus.PENDING);

        existingSub.ifPresent(subscriptionRepository::delete);

        Subscription subscription = SubscriptionMapper
                .toEntity(subscriptionDTO, currentUser, plan);

        subscription.initializeSubscriptionFromPlan();
        Subscription newSub = subscriptionRepository.save(subscription);


        return paymentGateway.createSubscriptionCheckoutSession(currentUser.getId(),
                                                                newSub.getId(),
                                                                plan.getStripePriceId());

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
    public Subscription findByCustomerIdAndStatus(String customerId, SubscriptionStatus status) {
        return subscriptionRepository
                .findByCustomerIdAndStatus(customerId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("El usuario actual no tiene subscripciones activas"));

    }

}
