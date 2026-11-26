package com.soracel.library_system.repository;

import com.soracel.library_system.domain.ReservationStatus;
import com.soracel.library_system.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.book.id = :bookId
    AND r.status = 'PENDING'
    """)
    int countPendingReservationsByBook(@Param("bookId") Long bookId);

    @Query("""
    SELECT r FROM Reservation r
    WHERE r.status = 'AVAILABLE'
    AND r.availableUntil < :currentDateTime
    """)
    List<Reservation> findExpiredReservations(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0
    THEN true ELSE false END
    FROM Reservation r
    WHERE r.user.id = :userId
    AND r.book.id = :bookId
    AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')
    """)
    boolean hasActiveReservation(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.user.id = :userId
    AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')
    """)
    int countActiveReservationsByUser(@Param("userId") Long userId);


    List<Reservation> findByBookIdAndStatusOrderByQueuePositionAsc(
            Long bookId,
            ReservationStatus status
    );

    @Query("""
    SELECT r FROM Reservation r
    WHERE (:userId IS NULL OR r.user.id = :userId)
    AND (:bookId IS NULL OR r.book.id = :bookId)
    AND (:status IS NULL OR r.status = :status)
    AND (:activeOnly = false OR (r.status = 'PENDING' OR r.status = 'AVAILABLE'))
    """)
    Page<Reservation> searchReservationWithFilters(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable
    );
}
