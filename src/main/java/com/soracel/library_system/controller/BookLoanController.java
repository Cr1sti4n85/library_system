package com.soracel.library_system.controller;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.payload.dto.BookLoanDTO;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.payload.request.RenewalRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @PostMapping
    public ResponseEntity<BookLoanDTO> checkoutBook(@Valid @RequestBody CheckoutRequest request) {
        BookLoanDTO bookLoanDTO = bookLoanService.checkoutBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookLoanDTO);
    }

    @PutMapping("/renewals")
    public ResponseEntity<BookLoanDTO>  renewCheckout(@Valid @RequestBody RenewalRequest request) {
        BookLoanDTO bookLoanDTO = bookLoanService.renewCheckout(request);
        return ResponseEntity.status(HttpStatus.OK).body(bookLoanDTO);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookLoanDTO>>  getMyLoans(
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<BookLoanDTO> bookLoans = bookLoanService.getUserBookLoans(status, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(bookLoans);
    }

}
