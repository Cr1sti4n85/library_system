package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO sp);

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO sp);

    void deleteSubscriptionPlan(Long planId);

    List<SubscriptionPlanDTO> getSubscriptionPlans();
}
