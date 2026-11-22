package com.soracel.library_system.model;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.domain.BookLoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_loans")
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @OneToMany(mappedBy = "bookLoan", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Fine> fines =  new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookLoanType loanType = BookLoanType.CHECKOUT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookLoanStatus status = BookLoanStatus.CHECKED_OUT;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Builder.Default
    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Column(nullable = false)
    private Integer maxRenewals;

    @Column(length = 150)
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOverdue = false;

    @Builder.Default
    @Column(nullable = false)
    private Long overdueDays = 0L;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return  this.status == BookLoanStatus.CHECKED_OUT || this.status == BookLoanStatus.OVERDUE;
    }

    public boolean canRenew() {
        return this.status == BookLoanStatus.CHECKED_OUT
                && !this.isOverdue
                && this.renewalCount <= this.maxRenewals;
    }

    public long calculateOverdueDays(LocalDate dueDate, LocalDate today) {
        return ChronoUnit.DAYS.between(dueDate, today);
    }

    public void addFine(Fine fine) {
        fines.add(fine);
        fine.setBookLoan(this);
    }
}
