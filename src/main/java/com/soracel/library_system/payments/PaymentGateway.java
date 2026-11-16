package com.soracel.library_system.payments;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Long userId, Long subscriptionId, String priceId);

    Optional<PaymentResult> parseWebhook(WebhookRequest request);

}
