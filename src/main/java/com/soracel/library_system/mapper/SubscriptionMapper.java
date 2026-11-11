package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.SubscriptionDTO;

import java.util.List;

public class SubscriptionMapper {

    public static SubscriptionDTO toDTO(Subscription subscription) {
        if  (subscription == null) return null;

        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .userName(subscription.getUser().getFullName())
                .email(subscription.getUser().getEmail())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlanName())
                .planCode(subscription.getPlanCode())
                .price(subscription.getPrice())
                .currency(subscription.getPlan().getCurrency())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .isActive(subscription.getIsActive())
                .maxBooksAllowed(subscription.getMaxBooksAllowed())
                .maxDaysPerBook(subscription.getMaxDaysPerBook())
                .autoRenew(subscription.getAutoRenew())
                .cancelledAt(subscription.getCancelledAt())
                .cancellationReason(subscription.getCancellationReason())
                .notes(subscription.getNotes())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .daysRemaining(subscription.getDaysRemaining())
                .isValid(subscription.isValid())
                .isExpired(subscription.isExpired())
                .build();
    }

    public static Subscription toEntity(SubscriptionDTO dto, User foundUser, SubscriptionPlan plan) {
        if  (dto == null) return null;

        Subscription.SubscriptionBuilder builder = Subscription.builder()
                .planName(dto.getPlanName())
                .planCode(dto.getPlanCode())
                .price(dto.getPrice())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .maxBooksAllowed(dto.getMaxBooksAllowed())
                .maxDaysPerBook(dto.getMaxDaysPerBook())
                .cancelledAt(dto.getCancelledAt())
                .cancellationReason(dto.getCancellationReason())
                .notes(dto.getNotes());

        if (dto.getIsActive() != null) {
            builder.isActive(dto.getIsActive());
        }
        if (dto.getAutoRenew() != null) {
            builder.autoRenew(dto.getAutoRenew());
        }

        builder.user(foundUser);
        builder.plan(plan);

        return builder.build();
    }

    public static List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions) {
        if (subscriptions == null) return null;
        return subscriptions.stream()
                .map(SubscriptionMapper::toDTO)
                .toList();
    }
}
