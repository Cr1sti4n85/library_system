package com.soracel.library_system.mapper;

import com.soracel.library_system.model.SubscriptionPlan;
import com.soracel.library_system.payload.dto.SubscriptionPlanDTO;

public class SubscriptionPlanMapper {

    public SubscriptionPlanDTO toDTO(SubscriptionPlan plan) {
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
                .isActive(plan.getIsActive())
                .badgeText(plan.getBadgeText())
                .isFeatured(plan.getIsFeatured())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .createdBy(plan.getCreatedBy())
                .updatedBy(plan.getUpdatedBy())
                .build();
    }

    public SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
        if (dto == null) return null;

        return SubscriptionPlan.builder()
                .id(dto.getId())
                .planCode(dto.getPlanCode())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .planName(dto.getPlanName())
                .durationDays(dto.getDurationDays())
                .maxBooksAllowed(dto.getMaxBooksAllowed())
                .maxDaysPerBook(dto.getMaxDaysPerBook())
                .displayOrder(dto.getDisplayOrder())
                .adminNotes(dto.getAdminNotes())
                .isActive(dto.getIsActive())
                .badgeText(dto.getBadgeText())
                .isFeatured(dto.getIsFeatured())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    public void updateEntity(SubscriptionPlan plan, SubscriptionPlanDTO dto) {
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

        dto.setUpdatedBy(dto.getUpdatedBy());
    }
}
