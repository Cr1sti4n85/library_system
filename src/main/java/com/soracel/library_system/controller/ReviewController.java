package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.BookReviewDTO;
import com.soracel.library_system.payload.request.CreateReviewRequest;
import com.soracel.library_system.payload.request.UpdateReviewRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService  reviewService;

    @PostMapping
    public ResponseEntity<BookReviewDTO> createReview(@Valid @RequestBody CreateReviewRequest request) {
        BookReviewDTO dto = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<BookReviewDTO> updateReview(@PathVariable Long reviewId, @Valid @RequestBody UpdateReviewRequest request) {
        BookReviewDTO dto = reviewService.updateReview(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBook(@PathVariable Long bookId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int pageSize)
    {
        PageResponse<BookReviewDTO> reviewPage = reviewService.getReviewsByBookId(bookId, page, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(reviewPage);

    }
}
