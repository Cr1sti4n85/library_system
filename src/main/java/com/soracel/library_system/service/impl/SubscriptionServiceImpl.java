package com.soracel.library_system.service.impl;

import com.soracel.library_system.domain.PaymentProvider;
import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.domain.PaymentType;
import com.soracel.library_system.domain.SubscriptionStatus;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.SubscriptionException;
import com.soracel.library_system.mapper.SubscriptionMapper;
import com.soracel.library_system.model.Payment;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.payments.PaymentGateway;
import com.soracel.library_system.payments.WebhookRequest;
import com.soracel.library_system.repository.PaymentRepository;
import com.soracel.library_system.repository.SubscriptionRepository;
import com.soracel.library_system.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;


    @Override
    public CheckoutSession subscribe(SubscriptionDTO subscriptionDTO) {

        User currentUser = userService.getCurrentUserEntity();
        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(subscriptionDTO.getPlanId());

        Subscription subscription = SubscriptionMapper
                .toEntity(subscriptionDTO, currentUser, plan);

        subscription.initializeSubscriptionFromPlan();
        Subscription newSub = subscriptionRepository.save(subscription);


        //TODO: START CHECKOUT SESSION
        return paymentGateway.createCheckoutSession(currentUser.getId(), newSub.getId(), plan.getStripePriceId());

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

    @Override
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhook(request).ifPresent(paymentResult -> {

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.PROCESSING)) {
                User user = userService.getUserById(paymentResult.getMetadata().get("userId"));
                user.setStripeCustomerId(paymentResult.getCustomerId()); //an update method in user service
                User savedUser = userRepository.save(user);

                Subscription sub = subscriptionRepository.findById(paymentResult.getMetadata().get("subscriptionId"))
                        .orElseThrow(() -> new NotFoundException("ID de subscription incorrecto"));
                sub.setCustomerId(paymentResult.getCustomerId());
                Subscription updatedSub = subscriptionRepository.save(sub);

                Payment payment = Payment.builder()  //crear Payment Service
                        .user(savedUser)
                        .subscription(updatedSub)
                        .paymentProvider(PaymentProvider.STRIPE) //temporary
                        .stripeCustomerId(paymentResult.getCustomerId())
                        .paymentType(PaymentType.MEMBERSHIP)
                        .status(PaymentStatus.PROCESSING)
                        .amount(sub.getPrice())
                        .build();
                paymentRepository.save(payment);

            }

            if (paymentResult.getPaymentStatus().equals(PaymentStatus.SUCCEEDED)){
                Subscription sub = subscriptionRepository
                        .findByCustomerIdAndStatus(paymentResult.getCustomerId(), SubscriptionStatus.PENDING)
                        .orElseThrow(() -> new NotFoundException("No se encuentra la suscripción"));
                sub.setIsActive(true);
                sub.setStatus(SubscriptionStatus.ACTIVE);
                Subscription updatedSub = subscriptionRepository.save(sub);

                User user = userRepository.findByStripeCustomerId(paymentResult.getCustomerId()) //quitar stripe del nombre
                        .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));
                Payment payment = paymentRepository
                                .getByUserSubscriptionAndStatus(
                                        user.getId(),
                                        updatedSub.getId(),
                                        PaymentStatus.PROCESSING)
                                .orElseThrow(() -> new NotFoundException("Pago no existe"));
                payment.setStatus(PaymentStatus.SUCCEEDED);
                paymentRepository.save(payment);

            }
            if (paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)){
                Subscription sub = subscriptionRepository
                        .findByCustomerIdAndStatus(paymentResult.getCustomerId(), SubscriptionStatus.PENDING)
                        .orElseThrow(() -> new NotFoundException("No se encuentra la suscripción"));
                sub.setStatus(SubscriptionStatus.PAYMENT_FAILED);
                Subscription updatedSub = subscriptionRepository.save(sub);

                Payment payment = paymentRepository
                        .getByUserSubscriptionAndStatus(
                                sub.getUser().getId(),
                                updatedSub.getId(),
                                PaymentStatus.PROCESSING)
                        .orElseThrow(() -> new NotFoundException("Pago no existe"));
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Hubo un error al procesar el pago");
                paymentRepository.save(payment);

                //TODO notify by email

            }
        });
    }
}
