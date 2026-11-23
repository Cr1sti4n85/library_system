package com.soracel.library_system.payments;

import com.soracel.library_system.model.Fine;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createSubscriptionCheckoutSession(Long userId, Long subscriptionId, String priceId);

    CheckoutSession createFineCheckoutSession(Fine fine);

    Optional<PaymentResult> parseWebhook(WebhookRequest request);

}
