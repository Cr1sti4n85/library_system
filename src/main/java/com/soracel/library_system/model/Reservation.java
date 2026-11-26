package com.soracel.library_system.model;

import com.soracel.library_system.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Book book;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    private LocalDateTime fulfilledAt;

    private LocalDateTime cancelledAt;

    private Integer queuePosition;

    @Column(nullable = false)
    @Builder.Default
    private boolean notificationSent = false;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean hasExpired() {
        return status == ReservationStatus.AVAILABLE
                && availableUntil != null
                && LocalDateTime.now().isAfter(availableUntil);
    }

    public boolean canBeCancelled() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.AVAILABLE;
    }
}
