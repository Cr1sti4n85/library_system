package com.soracel.library_system.service;

import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanService {
    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO spDTO);

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO spDTO);

    void deleteSubscriptionPlan(Long planId);

    List<SubscriptionPlanDTO> getSubscriptionPlans();

    SubscriptionPlan getSubscriptionPlanById(Long planId);
}
