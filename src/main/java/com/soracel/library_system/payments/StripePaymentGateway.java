package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.model.Fine;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    public CheckoutSession createSubscriptionCheckoutSession(Long userId, Long subscriptionId, String priceId) {
        StripeClient client = new StripeClient(secretKey);

        SessionCreateParams params = new SessionCreateParams.Builder()
                .setUiMode(SessionCreateParams.UiMode.HOSTED_PAGE)
                .setSuccessUrl(websiteUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(websiteUrl + "/cancel?session_id={CHECKOUT_SESSION_ID}")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                        .putMetadata("subscriptionId", String.valueOf(subscriptionId))
                        .putMetadata("userId", String.valueOf(userId))
                        .build())
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
    public CheckoutSession createFineCheckoutSession(Fine fine) {
        StripeClient client = new StripeClient(secretKey);

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setUiMode(SessionCreateParams.UiMode.HOSTED_PAGE)
                .setSuccessUrl(websiteUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(websiteUrl + "/cancel?session_id={CHECKOUT_SESSION_ID}")
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData
                        .builder()
                        .putMetadata("fineId", String.valueOf(fine.getId()))
                        .build())
                .putMetadata("fineId", String.valueOf(fine.getId()))
                .setSubmitType(SessionCreateParams.SubmitType.AUTO)
                .setIntegrationIdentifier("hosted_web_0003")
                .setOriginContext(SessionCreateParams.OriginContext.WEB);

        var lineItem = createLineItem(fine);
        builder.addLineItem(lineItem);
        try {
            Session session = client.v1().checkout().sessions().create(builder.build());

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

        try {
            Event event = client.constructEvent(payload, signature, webhookSecretKey);

            return switch (Objects.requireNonNull(event).getType()) {
                case "checkout.session.completed" ->
                    Optional.of(PaymentResult.builder()
                            .paymentStatus(PaymentStatus.COMPLETED)
                            .customerId(extractCustomerId(event))
                            .metadata(extractMetadata(event))
                            .paymentIntentId(extractPaymentIntent(event))
                            .subscriptionId(extractSubscriptionId(event))
                            .build());

                case "payment_intent.succeeded" ->
                        Optional.of(PaymentResult.builder()
                                .paymentStatus(PaymentStatus.SUCCEEDED)
                                .paymentIntentId(extractPaymentIntent(event))
                                .metadata(extractMetadata(event))
                                .paidAt(extractCreationPayment(event))
                                .customerId(extractCustomerId(event))
                                .build());

                case "invoice.paid" ->
                        Optional.of(PaymentResult.builder()
                                .paymentStatus(PaymentStatus.SUBSCRIPTION_SUCCEEDED)
                                .customerId(extractCustomerId(event))
                                .invoiceId(extractInvoiceId(event))
                                .metadata(extractMetadata(event))
                                .subscriptionId(extractSubscriptionId(event))
                                .paidAt(extractCreationPayment(event))
                                .subscriptionDates(extractSubscriptionDates(event))
                                .build());

                case "payment_intent.payment_failed" ->
                        Optional.of(PaymentResult.builder()
                                .paymentStatus(PaymentStatus.FAILED)
                                .paidAt(extractCreationPayment(event))
                                .metadata(extractMetadata(event))
                                .customerId(extractCustomerId(event))
                                .paymentIntentId(extractPaymentIntent(event))
                                .build());

                case "invoice.payment_failed" ->
                        Optional.of(PaymentResult.builder()
                                .paymentStatus(PaymentStatus.SUBSCRIPTION_FAILED)
                                .customerId(extractCustomerId(event))
                                .invoiceId(extractInvoiceId(event))
                                .metadata(extractMetadata(event))
                                .paidAt(extractCreationPayment(event))
                                .build());

                //TODO: add case for customer.subscription.updated when client cancels subscription

                default ->
                    Optional.empty();

            };
        } catch (SignatureVerificationException e) {
            // Invalid signature error 400
            throw new PaymentException("Firma no válida");
        }
    }

    private Map<String, Long> extractMetadata(Event event){
        Map<String, Long> metadata = new HashMap<>();
        if (event.getType().equals("checkout.session.completed")){
            Session session = deserializeStripeObject(event, Session.class);
            if (session.getMetadata().get("fineId") != null){
                Long fineId = Long.parseLong(session.getMetadata().get("fineId"));
                metadata.put("fineId", fineId);
            } else {
                Long userId = Long.parseLong(session.getMetadata().get("userId"));
                Long subscriptionId = Long.parseLong(session.getMetadata().get("subscriptionId"));
                metadata.put("subscriptionId", subscriptionId);
                metadata.put("userId", userId);
            }

        } else if (event.getType().startsWith("payment_intent")) {
            PaymentIntent intent = deserializeStripeObject(event, PaymentIntent.class);
            if (intent.getMetadata().get("fineId") != null){
                Long fineId =  Long.parseLong(intent.getMetadata().get("fineId"));
                metadata.put("fineId", fineId);
            }

        } else {
            Invoice invoice = deserializeStripeObject(event, Invoice.class);
            Long userId = Long.parseLong(invoice.getParent()
                        .getSubscriptionDetails()
                        .getMetadata().get("userId"));
            Long subscriptionId = Long.parseLong(invoice.getParent()
                                        .getSubscriptionDetails()
                                        .getMetadata().get("subscriptionId"));
            metadata.put("subscriptionId", subscriptionId);
            metadata.put("userId", userId);
        }
        return metadata;
    }

    //in one-time payments customerId = null, in subscriptions customerId != null
    private String extractCustomerId(Event event){
        if (event.getType().equals("checkout.session.completed")){
            Session session = deserializeStripeObject(event, Session.class);
            return session.getCustomer();
        } else if (event.getType().equals("invoice.paid") ||  event.getType().equals("invoice.payment_failed")){
            Invoice invoice = deserializeStripeObject(event, Invoice.class);
            return invoice.getCustomer();
        } else {
            PaymentIntent paymentIntent = deserializeStripeObject(event, PaymentIntent.class);
            return paymentIntent.getCustomer();
        }

    }

    private String extractSubscriptionId(Event event) {
        if (event.getType().equals("checkout.session.completed")){
            Session session = deserializeStripeObject(event, Session.class);
            return session.getSubscription();
        }
         Invoice invoice = deserializeStripeObject(event, Invoice.class);
        return invoice.getParent().getSubscriptionDetails().getSubscription();
    }

    private Map<String, LocalDate> extractSubscriptionDates(Event event) {
        Invoice session = deserializeStripeObject(event, Invoice.class);
        return session.getLines().getData()
                .stream()
                .map(item -> Map.of(
                        "startDate", Instant.ofEpochSecond(item.getPeriod()
                                .getStart()).atZone(ZoneOffset.UTC).toLocalDate(),
                        "endDate", Instant.ofEpochSecond(item.getPeriod()
                                .getEnd()).atZone(ZoneOffset.UTC).toLocalDate())).
                findFirst().orElse(null);
    }

    private String extractPaymentIntent(Event event) {
        if (event.getType().equals("checkout.session.completed")){
            Session session = deserializeStripeObject(event, Session.class);
            return session.getPaymentIntent();
        }
        PaymentIntent intent = deserializeStripeObject(event, PaymentIntent.class);
        return intent.getId();
    }

    private String extractInvoiceId(Event event) {
        Invoice invoice =  deserializeStripeObject(event, Invoice.class);
        return invoice.getId();
    }

    private LocalDateTime extractCreationPayment(Event event) {
        long unixSeconds;
        if (event.getType().startsWith("payment_intent")){
            PaymentIntent paymentIntent = deserializeStripeObject(event,  PaymentIntent.class);
            unixSeconds = paymentIntent.getCreated();
            return Instant.ofEpochSecond(unixSeconds).atZone(ZoneOffset.UTC).toLocalDateTime();
        }
        Invoice invoice =  deserializeStripeObject(event, Invoice.class);
        unixSeconds = invoice.getCreated();
        return Instant.ofEpochSecond(unixSeconds).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    private <T extends StripeObject> T deserializeStripeObject(Event event, Class<T> clazz) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException(
                        "Error al deserializar el objeto de Stripe"
                ));

        if (!clazz.isInstance(stripeObject)) {
            throw new PaymentException("El objeto de Stripe no corresponde al tipo esperado: "
                    + clazz.getSimpleName());
        }

        return clazz.cast(stripeObject);
    }

    private SessionCreateParams.LineItem createLineItem(Fine fine) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(createPriceData(fine))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(Fine fine) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("MXN")
                .setUnitAmountDecimal(fine.getAmount().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(fine.getFineReason()))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(String reason) {
        return SessionCreateParams
                .LineItem.PriceData.ProductData.builder()
                .setName(reason)
                //TODO mock image, implement library-related image
                .addImage("https://i.pinimg.com/736x/a7/7a/8b/a77a8b275f7c0894bcf0f8094fde7ad6.jpg")
                .build();
    }
}
