package com.soracel.library_system.mapper;

import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;

public class SubscriptionPlanMapper {

    public static SubscriptionPlanDTO toDTO(SubscriptionPlan plan) {
        if (plan == null) return null;

        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .planCode(plan.getPlanCode())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .planName(plan.getPlanName())
                .durationDays(plan.getDurationDays())
                .maxBooksAllowed(plan.getMaxBooksAllowed())
                .maxDaysPerBook(plan.getMaxDaysPerBook())
                .displayOrder(plan.getDisplayOrder())
                .adminNotes(plan.getAdminNotes())
                .currency(plan.getCurrency())
                .isActive(plan.getIsActive())
                .badgeText(plan.getBadgeText())
                .isFeatured(plan.getIsFeatured())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .createdBy(plan.getCreatedBy())
                .updatedBy(plan.getUpdatedBy())
                .build();
    }

    public static SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
        if (dto == null) return null;

        SubscriptionPlan.SubscriptionPlanBuilder builder = SubscriptionPlan.builder()
                .id(dto.getId())
                .planCode(dto.getPlanCode())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .planName(dto.getPlanName())
                .durationDays(dto.getDurationDays())
                .maxBooksAllowed(dto.getMaxBooksAllowed())
                .maxDaysPerBook(dto.getMaxDaysPerBook())
                .adminNotes(dto.getAdminNotes())
                .badgeText(dto.getBadgeText())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy());

        if (dto.getCurrency() != null)
            builder.currency(dto.getCurrency());

        if (dto.getDisplayOrder() != null)
            builder.displayOrder(dto.getDisplayOrder());

        if (dto.getIsActive() != null)
            builder.isActive(dto.getIsActive());

        if (dto.getIsFeatured() != null)
            builder.isFeatured(dto.getIsFeatured());

        return builder.build();
    }

    public static void updateEntityFromDTO(SubscriptionPlan plan, SubscriptionPlanDTO dto) {
        if (dto == null || plan == null) return;

        if (dto.getPlanName() != null) plan.setPlanName(dto.getPlanName());

        if  (dto.getDescription() != null) plan.setDescription(dto.getDescription());

        if (dto.getPrice() != null) plan.setPrice(dto.getPrice());

        if (dto.getDurationDays() != null) plan.setDurationDays(dto.getDurationDays());

        if (dto.getMaxBooksAllowed() != null) plan.setMaxBooksAllowed(dto.getMaxBooksAllowed());

        if (dto.getMaxDaysPerBook() != null) plan.setMaxDaysPerBook(dto.getMaxDaysPerBook());

        if (dto.getCurrency() != null) plan.setCurrency(dto.getCurrency());

        if (dto.getDisplayOrder() != null) plan.setDisplayOrder(dto.getDisplayOrder());

        if (dto.getAdminNotes() != null) plan.setAdminNotes(dto.getAdminNotes());

        if (dto.getIsActive() != null )  plan.setIsActive(dto.getIsActive());

        if (dto.getIsFeatured() != null) plan.setIsFeatured(dto.getIsFeatured());

        if (dto.getBadgeText() != null) plan.setBadgeText(dto.getBadgeText());

    }
}
