package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentStatus;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    @Value("${stripe.secret-key}")
    private String secretKey;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecretKey;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(Long userId, Long subscriptionId, String priceId) {
        StripeClient client = new StripeClient(secretKey);

        SessionCreateParams params = new SessionCreateParams.Builder()
                .setUiMode(SessionCreateParams.UiMode.HOSTED_PAGE)
                .setSuccessUrl(websiteUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(websiteUrl + "/cancel?session_id={CHECKOUT_SESSION_ID}")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .putMetadata("subscriptionId", String.valueOf(subscriptionId))
                .putMetadata("userId", String.valueOf(userId))
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build()
                )
                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.AUTO)
                .setPaymentMethodCollection(SessionCreateParams.PaymentMethodCollection.ALWAYS)
                .setAllowPromotionCodes(false)
                .setSubmitType(SessionCreateParams.SubmitType.AUTO)
                .setIntegrationIdentifier("hosted_web_0002")
                .setOriginContext(SessionCreateParams.OriginContext.WEB)
                .build();
        
        try {
            Session session = client.v1().checkout().sessions().create(params);

            return new CheckoutSession(session.getUrl());
        } catch (StripeException e) {
            log.error(e.toString());
            throw new PaymentException("Ocurrió un error al procesar el pago");
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhook(WebhookRequest request){
        StripeClient client = new StripeClient(secretKey);

        var payload = request.getPayload();
        var signature = request.getHeaders().get("Stripe-Signature");

//        Event event = null;
        try {
            Event event = client.constructEvent(payload, signature, webhookSecretKey);

            return switch (Objects.requireNonNull(event).getType()) {
                case "checkout.session.completed" ->
                    Optional.of(PaymentResult.builder()
                            .paymentStatus(PaymentStatus.COMPLETED)
                            .customerId(extractCustomerId(event))
                            .metadata(extractMetadata(event))
                            .build());

                case "invoice.paid" ->
                     Optional.of(PaymentResult.builder()
                            .paymentStatus(PaymentStatus.SUCCEEDED)
                             .customerId(extractCustomerId(event))
                            .build());

                case "invoice.payment_failed" ->
                    Optional.of(PaymentResult.builder()
                            .paymentStatus(PaymentStatus.FAILED)
                            .customerId(extractCustomerId(event))
                            .build());

                default ->
                    Optional.empty();

            };
        } catch (SignatureVerificationException e) {
            // Invalid signature error 400
            throw new PaymentException("Firma no válida");
        }
    }

    private Map<String, Long> extractMetadata(Event event){
            Session session = deserializeSession(event);
            Long userId = Long.parseLong(session.getMetadata().get("userId"));
            Long subscriptionId = Long.parseLong(session.getMetadata().get("subscriptionId"));
        return Map.of(
                "userId", userId,
                "subscriptionId", subscriptionId
        );
    }

    private String extractCustomerId(Event event){
        if (event.getType().equals("checkout.session.completed")){
            Session session = deserializeSession(event);
            return session.getCustomer();

        }
            Invoice invoice = deserializeInvoice(event);
            return invoice.getCustomer();
    }


    private Session deserializeSession(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException("Error al deserializar el objeto de Stripe"));

        return (Session) stripeObject;
    }

    private Invoice deserializeInvoice(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException("Error al deserializar el objeto de Stripe"));

        return (Invoice) stripeObject;
    }
}
