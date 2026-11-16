package com.soracel.library_system.repository;

import com.soracel.library_system.domain.PaymentStatus;
import com.soracel.library_system.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(
            """
    SELECT p from Payment p
    WHERE p.user.id = :userId
    AND p.subscription.id = :subscriptionId
    AND p.status = :status
    """
    )
    Optional<Payment> getByUserSubscriptionAndStatus(@Param("userId") Long userId,
                                                     @Param("subscriptionId") Long subscriptionId,
                                                     @Param("status") PaymentStatus status);
}
