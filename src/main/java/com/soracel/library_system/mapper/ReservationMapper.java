package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Reservation;
import com.soracel.library_system.payload.dto.ReservationDTO;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReservationMapper {
    public static ReservationDTO toDTO(Reservation reservation) {
        if  (reservation == null) {
            return null;
        }
        ReservationDTO.ReservationDTOBuilder builder = ReservationDTO.builder()
                .id(reservation.getId())
                .status(reservation.getStatus())
                .userId(reservation.getUser().getId())
                .username(reservation.getUser().getFullName())
                .email(reservation.getUser().getEmail())
                .bookId(reservation.getBook().getId())
                .bookTitle(reservation.getBook().getTitle())
                .bookAuthor(reservation.getBook().getAuthor())
                .bookIsbn(reservation.getBook().getIsbn())
                .availableAt(reservation.getAvailableAt())
                .reservedAt(reservation.getReservedAt())
                .fulfilledAt(reservation.getFulfilledAt())
                .cancelledAt(reservation.getCancelledAt())
                .queuePosition(reservation.getQueuePosition())
                .notificationSent(reservation.isNotificationSent())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .isExpired(reservation.hasExpired())
                .canBeCancelled(reservation.canBeCancelled());

        if (reservation.getAvailableUntil() != null) {
            if (LocalDateTime.now().isBefore(reservation.getAvailableUntil())) {
                long hours = Duration.between(LocalDateTime.now(), reservation.getAvailableUntil()).toHours();
                builder.hoursUntilExpiry((int)hours);
            } else {
                builder.hoursUntilExpiry(0);
            }
        }

        return  builder.build();

    }
}
