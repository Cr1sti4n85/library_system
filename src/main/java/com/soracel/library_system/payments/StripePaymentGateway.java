package com.soracel.library_system.payments;

import com.soracel.library_system.domain.PaymentStatus;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    @Value("${stripe.secret-key}")
    private String secretKey;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecretKey;

    @Value("{websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(String priceId) {
        StripeClient client = new StripeClient(secretKey);

        SessionCreateParams params = new SessionCreateParams.Builder()
                .setUiMode(SessionCreateParams.UiMode.HOSTED_PAGE)
                .setSuccessUrl(websiteUrl + "/?session_id={CHECKOUT_SESSION_ID}")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
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
            log.error(e.getMessage());
            throw new PaymentException("Ocurrió un error al procesar el pago");
        }
    }

    @Override
    public PaymentResult parseWebhook(WebhookRequest request){
        StripeClient client = new StripeClient(secretKey);

        var payload = request.getPayload();
        var signature = request.getHeaders().get("stripe-signature");


        Event event = null;
        try {
            event = client.constructEvent(payload, signature, webhookSecretKey);
            return switch (Objects.requireNonNull(event).getType()) {
                case "checkout.session.completed" ->
                     PaymentResult.builder()
                            .paymentStatus(PaymentStatus.COMPLETED)
                            .build();
                    //TODO Provision the subscription and save the customer ID to your database.


                case "invoice.paid" ->
                    PaymentResult.builder()
                            .paymentStatus(PaymentStatus.SUCCEEDED)
                            .build();

                //TODO Continue to provision the subscription as payments continue to be made.
                // Store the status in your database and check when a customer accesses your service.
                // This approach helps you avoid hitting rate limits.
                case "invoice.payment_failed" ->
                    PaymentResult.builder()
                            .paymentStatus(PaymentStatus.FAILED)
                            .build();

                //TODO The payment failed or the customer doesn't have a valid payment method.
                // The subscription becomes past_due. Notify your customer and send them to the
                // customer portal to update their payment information.

                default ->
                        throw new IllegalStateException("Unexpected value: " + event.getType());
            };
        } catch (SignatureVerificationException e) {
            // Invalid signature error 400
            throw new PaymentException("Firma no válida");
        }
    }
}
