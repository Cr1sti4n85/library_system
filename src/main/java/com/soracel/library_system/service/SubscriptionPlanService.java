package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO spDTO);

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO spDTO);

    void deleteSubscriptionPlan(Long planId);

    List<SubscriptionPlanDTO> getSubscriptionPlans();
}
