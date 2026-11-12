package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.SubscriptionDTO;
import com.soracel.library_system.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDTO> subscribe(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO dto =  subscriptionService.subscribe(subscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
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

}
