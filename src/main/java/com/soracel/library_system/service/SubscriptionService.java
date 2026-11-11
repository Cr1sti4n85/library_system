package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.SubscriptionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

    SubscriptionDTO activeSubscription(Long subscriptionId, Long payment);

    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);
}
