package com.soracel.library_system.payload.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionPlanDTO {

    private Long id;

    @NotNull(message = "El plan es obligatorio")
    private String planCode;

    @NotNull(message = "El nombre es obligatorio")
    private String planName;

    private String description;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser mayor a cero")
    private Integer durationDays;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe tener valor positivo")
    private Long price;

    private String currency;

    @NotNull(message = "La cantidad de libros máxima es obligatoria")
    @Positive(message = "La cantidad de libros debe ser mayor a cero")
    private Integer maxBooksAllowed;

    @NotNull(message = "La cantidad máxima de días por libro es obligatoria")
    @Positive(message = "La cantidad máxima de días por libro debe ser mayor a cero")
    private Integer maxDaysPerBook;

    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isFeatured;
    private String badgeText;
    private String adminNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}
