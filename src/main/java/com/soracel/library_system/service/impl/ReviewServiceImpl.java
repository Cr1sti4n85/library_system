package com.soracel.library_system.service.impl;

import com.soracel.library_system.common.pagination.PaginationUtil;
import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.domain.BookLoanType;
import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.exception.ForbiddenUserException;
import com.soracel.library_system.exception.ReviewException;
import com.soracel.library_system.mapper.ReviewMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.Review;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.BookReviewDTO;
import com.soracel.library_system.payload.request.CreateReviewRequest;
import com.soracel.library_system.payload.request.UpdateReviewRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookLoanRepository;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.ReviewRepository;
import com.soracel.library_system.service.ReviewService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookLoanRepository  bookLoanRepository;

    @Override
    public BookReviewDTO createReview(CreateReviewRequest request) {
        User currentUser = userService.getCurrentUserEntity();

        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new BookException("El id del libro no es válido")
        );

        if (reviewRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId())){
            throw new ReviewException("Ya calificaste este libro");
        }

        boolean hasReadBook = hasUserReadBook(currentUser.getId(), book.getId());

        if (!hasReadBook) {
            throw new ReviewException("No puedes calificar un libro que no has solicitado");
        }

        Review review = Review.builder()
                .user(currentUser)
                .book(book)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .title(request.getTitle())
                .build();

        Review newReview = reviewRepository.save(review);

        return ReviewMapper.toDTO(newReview);
    }

    @Override
    public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {
        User currentUser = userService.getCurrentUserEntity();

        Review existingReview = getReviewByIdAndUser(reviewId, currentUser);

        existingReview.setReviewText(request.getReviewText());
        existingReview.setTitle(request.getTitle());
        existingReview.setRating(request.getRating());

        return ReviewMapper.toDTO(reviewRepository.save(existingReview));
    }

    @Override
    public void deleteReview(Long reviewId) {
        User currentUser = userService.getCurrentUserEntity();
        Review review = getReviewByIdAndUser(reviewId, currentUser);

        reviewRepository.delete(review);
    }

    @Override
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt", "DESC");
        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable);

        return PaginationUtil.convertToPageResponse(reviewPage, ReviewMapper::toDTO);
    }

    private boolean hasUserReadBook(Long userId, Long bookId) {

        return bookLoanRepository.findByUserIdAndBookId(userId, bookId).
                stream().anyMatch(bl -> bl.getLoanType() == BookLoanType.RETURN);
    }

    private Review getReviewByIdAndUser(Long reviewId, User currentUser) {
        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException("El id de la calificación no es válida")
        );
        if (!currentUser.getId().equals(existingReview.getUser().getId())) {
            throw new ForbiddenUserException("No estás autorizado para eliminar/actualizar esta calificación");
        }

        return existingReview;
    }
}
