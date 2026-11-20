package com.soracel.library_system.service.impl;

import com.soracel.library_system.common.pagination.PaginationUtil;
import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.domain.BookLoanType;
import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.exception.LoanException;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.exception.SubscriptionException;
import com.soracel.library_system.mapper.BookLoanMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.BookLoan;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.BookLoanDTO;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payload.request.BookLoanSearchRequest;
import com.soracel.library_system.payload.request.CheckinRequest;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.payload.request.RenewalRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookLoanRepository;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.projection.BookLoanStatusCount;
import com.soracel.library_system.service.BookLoanService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final BookRepository  bookRepository;
    private final UserService userService;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest request) {

        UserDTO user = userService.getCurrentUser();

        return checkoutBookForUser(user.getId(), request);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest request) {
        User user = userService.getUserById(userId);

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

        //SAVE BOOK LOAN
        BookLoan savedLoan = bookLoanRepository.save(bookLoan);

        return BookLoanMapper.toDTO(savedLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest request) {
        //VALIDATE BOOK LOAN EXISTS
        BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(() -> new LoanException("No existe préstamo asociado a ese libro"));

        //check if book alreay returned
        if (!loan.isActive()) {
            throw new LoanException("No existe préstamo asociado a este libro");
        }

        //set returned properties
        loan.setReturnDate(LocalDate.now());
        BookLoanStatus status =  request.getStatus();

        if (status == null) status = BookLoanStatus.RETURNED;

        loan.setStatus(status);
        loan.setLoanType(BookLoanType.RETURN);
        loan.setNotes(request.getNotes());
        loan.setIsOverdue(false);
        loan.setOverdueDays(0);

        //TODO apply fine if the status is OVERDUE

        //update book availability
        if (status != BookLoanStatus.LOST){
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            //TODO process next checkin
        }

        BookLoan savedLoan = bookLoanRepository.save(loan);
        return BookLoanMapper.toDTO(savedLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest request) {
        //VALIDATE BOOK LOAN EXISTS
        BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(() -> new LoanException("No existe préstamo asociado a ese libro"));

        //CHECK IF BOOK CAN BE RENEWED - LOAN renewalCount, maxRenewals (basic 1; premium 2)
        if (!loan.canRenew()){
            throw new LoanException("El préstamo del libro no puede renovarse." +
                    "Revisa si tienes multa o si agotaste tus renovaciones");
        }
        //update dueDate
        Subscription subscription = loan.getUser().getSubscriptions()
                                    .stream()
                                    .filter(sub -> sub.getIsActive() == true)
                                    .findFirst()
                                    .orElseThrow(() -> new SubscriptionException("No tiene suscripciones activas."));

        loan.setDueDate(loan.getDueDate().plusDays(subscription.getMaxDaysPerBook()));

        loan.setLoanType(BookLoanType.RENEWAL);

        //increment renewal count
        loan.setRenewalCount(loan.getRenewalCount() + 1);
        loan.setNotes(request.getNotes());

        BookLoan renewal = bookLoanRepository.save(loan);

        return BookLoanMapper.toDTO(renewal);
    }

    @Override
    public PageResponse<BookLoanDTO> getUserBookLoans(BookLoanStatus status, int page, int size) {
        User currentUser = userService.getCurrentUserEntity();
        Page<BookLoan> bookLoanPage;

        if (status != null){
            Pageable pageable = PaginationUtil.createPageable(page, size, "dueDate", "ASC");
            bookLoanPage = bookLoanRepository.findByUserAndStatus(currentUser, status, pageable);
        } else {
            Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt", "DESC");
            bookLoanPage =  bookLoanRepository.findByUserId(currentUser.getId(), pageable);
        }
        return PaginationUtil.convertToPageResponse(bookLoanPage, BookLoanMapper::toDTO);
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) {

        Pageable pageable = PaginationUtil.createPageable(
                request.getPage() != null ? request.getPage() : 0,
                request.getPageSize() != null ? request.getPageSize() : 20,
                request.getSortBy()  != null ? request.getSortBy() : "createdAt",
                request.getSortDirection()  != null ? request.getSortDirection() : "DESC"
        );
        Page<BookLoan> bookLoanPage;

        if(request.getOverdueOnly() != null && request.getOverdueOnly()){
            bookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        } else if (request.getUserId() != null) {
            bookLoanPage = bookLoanRepository.findByUserId(request.getUserId(), pageable);
        } else if (request.getBookId() != null) {
            bookLoanPage = bookLoanRepository.findByBookId(request.getBookId(), pageable);
        } else if (request.getStatus() != null) {
            bookLoanPage = bookLoanRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            bookLoanPage = bookLoanRepository.findBookByDateRange(
                    request.getStartDate(),
                    request.getEndDate(),
                    pageable
            );
        } else {
            bookLoanPage = bookLoanRepository.findAll(pageable);
        }

        return PaginationUtil.convertToPageResponse(bookLoanPage, BookLoanMapper::toDTO);
    }

    @Override
    public Integer updateOverdueBookLoan() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<BookLoan> overdueBookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        int updateCount = 0;

        for (BookLoan bookLoan : overdueBookLoanPage.getContent()) {
            if (bookLoan.getStatus().equals(BookLoanStatus.CHECKED_OUT)) {
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);
            }
//            Calculate overdue days
            int overdueDays= bookLoan.calculateOverdueDays(bookLoan.getDueDate(), LocalDate.now());
            bookLoan.setOverdueDays(overdueDays);

//            TODO Calculate fine
            bookLoanRepository.save(bookLoan);
            updateCount++;
        }
        return updateCount;
    }

}
