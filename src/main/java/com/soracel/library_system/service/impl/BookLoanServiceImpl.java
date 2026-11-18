package com.soracel.library_system.service.impl;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.exception.LoanException;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.SubscriptionException;
import com.soracel.library_system.mapper.BookLoanMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.BookLoan;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.dto.BookLoanDTO;
import com.soracel.library_system.payload.request.BookLoanSearchRequest;
import com.soracel.library_system.payload.request.CheckinRequest;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.payload.request.RenewalRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookLoanRepository;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.projection.BookLoanStatusCount;
import com.soracel.library_system.service.BookLoanService;
import com.soracel.library_system.service.SubscriptionService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final BookRepository  bookRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest request) {
        return null;
    }

    @Override
    public BookLoanDTO checkoutBookForUser(CheckoutRequest request) {
        User user = userService.getUserById(request.getUserId());

        //VALIDATE ACTIVE SUBSCRIPTION
//        SubscriptionDTO subscription = subscriptionService.getUsersActiveSubscription(user.getId());

        Subscription subscription = user.getSubscriptions()
                                    .stream()
                                    .filter(sub -> sub.getIsActive() == true)
                                    .findFirst()
                                    .orElseThrow(() -> new SubscriptionException("No tiene suscripciones activas."));

        //VALIDATE BOOK EXISTS AND IS AVAILABLE
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));

        if (!book.getActive() || book.getAvailableCopies() == 0){
            throw new BookException("El libro no está disponible");
        }

        //CHECK IF USER ALREADY CHECKED OUT THE BOOK
        if (bookLoanRepository.hasActiveCheckout(user.getId(), book.getId())){
            throw new LoanException("El libro ya fue prestado al usuario");
        }

        //CHECK USER'S ACTIVE CHECKOUT LIMIT (maxBooksAllowed)
        long activeCheckouts = bookLoanRepository.countActiveBookLoansByUser(user.getId());
        int maxBooksAllowed = subscription.getMaxBooksAllowed();
        if (activeCheckouts >= maxBooksAllowed){
            throw new LoanException("Ha alcanzado la cantidad máxima de préstamos");
        }

        //CHECK FOR OVERDUE BOOKS
        List<BookLoanStatusCount> statusCountResults = bookLoanRepository.countBookLoansByStatus(user.getId());
        long overdueCount = statusCountResults.stream()
                .filter(r -> r.getStatus() == BookLoanStatus.OVERDUE)
                .mapToLong(BookLoanStatusCount::getTotal)
                .findFirst()
                .orElse(0L);

        if (overdueCount > 0) {
            throw new LoanException("Tienes préstamos vencidos que no han sido retornados.");
        }
        //TODO check for fines when implemented

        //UPDATE AVAILABLE BOOK COPIES AND CREATE LOAN
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Book updatedBook = bookRepository.save(book);

        //CREATE BOOK LOAN
        BookLoan bookLoan = BookLoan.builder()
                .user(user)
                .book(updatedBook)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(subscription.getMaxDaysPerBook()))
                .maxRenewals(subscription.getPlan().getMaxRenewals())
                .notes(request.getNotes())
                .build();

        System.out.println("Prestamo: " + bookLoan);

        //SAVE BOOK LOAN
        BookLoan savedLoan = bookLoanRepository.save(bookLoan);

        return BookLoanMapper.toDTO(savedLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest request) {
        return null;
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest request) {
        return null;
    }

    @Override
    public PageResponse<BookDTO> getUserBookLoans(BookLoanStatus status, int page, int size) {
        return null;
    }

    @Override
    public PageResponse<BookDTO> getBookLoans(BookLoanSearchRequest request) {
        return null;
    }

    @Override
    public Integer updateOverdueBookLoan() {
        return 0;
    }
}
