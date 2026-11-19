package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.BookLoanDTO;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @PostMapping
    public ResponseEntity<BookLoanDTO> checkoutBook(@Valid @RequestBody CheckoutRequest request) {
//        BookLoanDTO bookLoanDTO = bookLoanService.checkoutBookForUser(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(bookLoanDTO);

    }
}
