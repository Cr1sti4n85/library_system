package com.soracel.library_system.service;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.dto.BookLoanDTO;
import com.soracel.library_system.payload.request.BookLoanSearchRequest;
import com.soracel.library_system.payload.request.CheckinRequest;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.payload.request.RenewalRequest;
import com.soracel.library_system.payload.response.PageResponse;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest request);

    BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest request);

    BookLoanDTO checkinBook(CheckinRequest request);

    BookLoanDTO renewCheckout(RenewalRequest request);

    PageResponse<BookLoanDTO> getUserBookLoans(
            BookLoanStatus status,
            int page,
            int size
    );
    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);

    Integer updateOverdueBookLoan();

    boolean checkLoanExistsForUser(Long userId, Long bookId);

}
