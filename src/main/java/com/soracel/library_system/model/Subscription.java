package com.soracel.library_system.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionPlan plan;

    @OneToMany(mappedBy = "subscription",  cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    private String planName;

    private String planCode;

    private BigDecimal price;

    @Column(nullable = false)
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    private Integer maxDaysPerBook;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean autoRenew = false;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Boolean isValid() {
        if (!isActive) return false;

        LocalDate today =  LocalDate.now();

        return !today.isBefore(this.startDate) && !today.isAfter(this.endDate);
    }

    public Boolean isExpired(){
        return LocalDate.now().isAfter(this.endDate);
    }

    public long getDaysRemaining(){
        if (isExpired()) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), this.endDate);
    }

    public void calculateEndDate() {
        if (plan != null && startDate != null) {
            this.endDate = startDate.plusDays(plan.getDurationDays());
        }
    }

    public void initializeSubscriptionFromPlan() {
        if (plan != null) {
            this.planName = plan.getPlanName();
            this.planCode = plan.getPlanCode();
            this.price = plan.getPrice();
            this.maxBooksAllowed = plan.getMaxBooksAllowed();
            this.maxDaysPerBook = plan.getMaxDaysPerBook();

            if (startDate == null) {
                this.startDate = LocalDate.now();
            }
            calculateEndDate();
        }
    }

}
