package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Review;
import com.soracel.library_system.payload.dto.BookReviewDTO;

public class ReviewMapper {

    public static BookReviewDTO toDTO(Review review) {
        if  (review == null) {
            return null;
        }
        return BookReviewDTO.builder()
                .id(review.getId())
                .userName(review.getUser().getFullName())
                .userId(review.getUser().getId())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .title(review.getTitle())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
