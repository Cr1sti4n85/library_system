package com.soracel.library_system.model;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.domain.BookLoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private BookLoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookLoanStatus status;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Builder.Default
    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer maxRenewals = 2;

    @Column(length = 150)
    private String notes;

    //private BigDecimal fine;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOverdue = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer overdueDays = 0;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
