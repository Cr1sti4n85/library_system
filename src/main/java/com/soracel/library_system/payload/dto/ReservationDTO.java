package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.ReservationStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long id;

    private Long userId;

    private Long bookId;

    private ReservationStatus status;

    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    private LocalDateTime fulfilledAt;

    private LocalDateTime cancelledAt;

    @Positive(message = "El valor de la posición debe ser positivo")
    private Integer queuePosition;

    private boolean notificationSent;

    @Size(max = 150, message = "La nota debe tener un máximo de 150 caracteres")
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
