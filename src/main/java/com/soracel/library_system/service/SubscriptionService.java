package com.soracel.library_system.service;

import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.payments.PaymentResult;
import com.soracel.library_system.payments.WebhookRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    CheckoutSession subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

    void activateSubscription(PaymentResult result);

    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);

    void deactivateExpiredSubscriptions();

    void handleWebhookEvent(WebhookRequest request);
}
