package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.payments.WebhookRequest;
import com.soracel.library_system.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<CheckoutSession> subscribe(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        CheckoutSession session =  subscriptionService.subscribe(subscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SubscriptionDTO> getUsersActiveSubscription(@PathVariable Long userId) {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) String reason
    ) {
        SubscriptionDTO dto = subscriptionService.cancelSubscription(subscriptionId, reason);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PatchMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDTO> activateSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam Long paymentId
    ) {
        SubscriptionDTO dto = subscriptionService.activateSubscription(subscriptionId, paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ){
        subscriptionService.handleWebhookEvent(new WebhookRequest(headers, payload));
    }

}
