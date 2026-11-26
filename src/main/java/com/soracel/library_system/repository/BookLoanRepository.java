package com.soracel.library_system.repository;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.model.BookLoan;
import com.soracel.library_system.model.User;
import com.soracel.library_system.repository.projection.BookLoanStatusCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    Page<BookLoan> findByUserId(Long userId, Pageable pageable);
    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);
    Page<BookLoan> findByUserAndStatus(User user, BookLoanStatus status, Pageable pageable);

    @Query("""
        SELECT CASE
        WHEN COUNT(bl) > 0
        THEN true
        ELSE false END
        FROM BookLoan bl
        WHERE bl.user.id = :userId
        AND bl.book.id = :bookId
        AND (bl.status = "CHECKED_OUT" OR bl.status = "OVERDUE")
        """
    )
    boolean hasActiveCheckout(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("""
        SELECT COUNT(bl) FROM BookLoan bl
        WHERE bl.user.id = :userId
        AND (bl.status = "CHECKED_OUT" OR bl.status = "OVERDUE")
        """
    )
    long countActiveBookLoansByUser(@Param("userId") Long userId);


    @Query("""
    SELECT bl.status AS status, COUNT(bl) AS total
    FROM BookLoan bl
    WHERE bl.user.id = :userId
    AND bl.status IN ('CHECKED_OUT','RETURNED','OVERDUE','LOST','DAMAGED')
    GROUP BY bl.status
    """)
    List<BookLoanStatusCount> countBookLoansByStatus(@Param("userId") Long userId);

    @Query("""
    SELECT bl FROM BookLoan bl
    WHERE bl.dueDate < :currentDate
    AND (bl.status = "CHECKED_OUT" OR bl.status = "OVERDUE")
    """)
    Page<BookLoan> findOverdueBookLoans(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    @Query("""
        SELECT bl FROM BookLoan bl
        WHERE bl.checkoutDate BETWEEN :startDate AND :endDate
    """)
    Page<BookLoan> findBookByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    List<BookLoan> findByUserIdAndBookId(Long userId, Long bookId);
}
