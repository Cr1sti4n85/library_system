package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;
import com.soracel.library_system.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getSubscriptionPlans() {
        return ResponseEntity.ok(subscriptionPlanService.getSubscriptionPlans());
    }

}
