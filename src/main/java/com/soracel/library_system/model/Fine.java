package com.soracel.library_system.model;

import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_loan_id", nullable = false)
    private BookLoan bookLoan;

    @OneToMany(mappedBy = "fine", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private FineType fineType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private FineStatus status;

    @Column(length = 100)
    private String fineReason;

    @Column(length = 100)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waived_by_user_id")
    private User waivedBy;

    private LocalDateTime waivedAt;

    @Column(length = 150)
    private String waiverReason;

    private LocalDateTime paidAt;

    private String paymentIntentId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public void waive(User currentAdmin, String reason) {
        this.status = FineStatus.WAIVED;
        this.waivedBy = currentAdmin;
        this.waiverReason = reason;
        this.waivedAt = LocalDateTime.now();
    }
}
