package com.soracel.library_system.repository;

import com.soracel.library_system.model.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Page<WishList> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long id, Long id1);

    void deleteByUserIdAndBookId(Long id, Long bookId);
}
