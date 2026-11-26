package com.soracel.library_system.repository;

import com.soracel.library_system.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBookId(Long bookId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long uerId, Long bookId);
}
