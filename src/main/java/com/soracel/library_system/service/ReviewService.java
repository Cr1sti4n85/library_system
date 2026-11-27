package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.BookReviewDTO;
import com.soracel.library_system.payload.request.CreateReviewRequest;
import com.soracel.library_system.payload.request.UpdateReviewRequest;
import com.soracel.library_system.payload.response.PageResponse;
import org.springframework.data.domain.PageRequest;

public interface ReviewService {
    BookReviewDTO createReview(CreateReviewRequest request);

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);

    PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size);
}
