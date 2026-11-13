package com.soracel.library_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String planCode;

    @Column(nullable = false, unique = true, length = 50)
    private String planName;

    private String description;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    private String currency = "MXN";

    @Column(nullable = false)
    @Positive(message = "Cantidad de libros debe ser positiva")
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    @Positive(message = "Cantidad de días debe ser positiva")
    private Integer maxDaysPerBook;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isFeatured = false;

    private String badgeText;

    private String adminNotes;

    @OneToMany(mappedBy = "plan",  cascade = CascadeType.PERSIST)
    private Set<Subscription> subscriptions = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
