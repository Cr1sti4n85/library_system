package com.soracel.library_system.service.impl;

import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;
import com.soracel.library_system.repository.SubscriptionPlanRepository;
import com.soracel.library_system.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository  subscriptionPlanRepository;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO sp) {

        return null;
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO sp) {
        return null;
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) {

    }

    @Override
    public List<SubscriptionPlanDTO> getSubscriptionPlans() {
        return List.of();
    }
}
