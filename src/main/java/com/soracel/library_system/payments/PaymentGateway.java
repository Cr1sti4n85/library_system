package com.soracel.library_system.payments;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(String priceId);

    PaymentResult parseWebhook(WebhookRequest request);

}
