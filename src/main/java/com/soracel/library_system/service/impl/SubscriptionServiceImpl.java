package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.SubscriptionException;
import com.soracel.library_system.mapper.SubscriptionMapper;
import com.soracel.library_system.mapper.SubscriptionPlanMapper;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;
import com.soracel.library_system.payload.dto.UserDTO;
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
    private final UserService userService;
    private final SubscriptionPlanService  subscriptionPlanService;


    @Override
    public SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO) {

        //get the user and the subscription plan
        User currentUser = userService.getCurrentUserEntity();
        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(subscriptionDTO.getPlanId());

        Subscription subscription = SubscriptionMapper
                .toEntity(subscriptionDTO, currentUser, plan);

        subscription.initializeSubscriptionFromPlan();
        subscription.setIsActive(false); //remove this after payment implementation
        //TODO: CREATE PAYMENT

        Subscription newSub = subscriptionRepository.save(subscription);

        return SubscriptionMapper.toDTO(newSub);


    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) {
        UserDTO userDTO = userService.getCurrentUser();
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userDTO.getId(), LocalDate.now())
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
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long payment) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("ID de subscripción incorrecto"));

        //TODO: Verify payment

        subscription.setIsActive(true);

        Subscription activatedSubscription = subscriptionRepository.save(subscription);
        return SubscriptionMapper.toDTO(activatedSubscription);
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
}
