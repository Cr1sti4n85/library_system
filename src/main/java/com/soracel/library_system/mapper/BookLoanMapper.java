package com.soracel.library_system.mapper;

import com.soracel.library_system.model.BookLoan;
import com.soracel.library_system.payload.dto.BookLoanDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookLoanMapper {
    public static BookLoanDTO toDTO(BookLoan bookLoan) {
        if(bookLoan == null) return null;

        return BookLoanDTO.builder()
                .id(bookLoan.getId())
                .userId(bookLoan.getUser().getId())
                .userEmail(bookLoan.getUser().getEmail())
                .userName(bookLoan.getUser().getFullName())
                .bookId(bookLoan.getBook().getId())
                .bookTitle(bookLoan.getBook().getTitle())
                .bookIsbn(bookLoan.getBook().getIsbn())
                .bookAuthor(bookLoan.getBook().getAuthor())
                .bookCoverImage(bookLoan.getBook().getCoverImageUrl())
                .loanType(bookLoan.getLoanType())
                .status(bookLoan.getStatus())
                .checkoutDate(bookLoan.getCheckoutDate())
                .dueDate(bookLoan.getDueDate())
                .remainingDays(ChronoUnit.DAYS.between(LocalDate.now(), bookLoan.getDueDate()))
                .returnDate(bookLoan.getReturnDate())
                .renewalCount(bookLoan.getRenewalCount())
                .maxRenewals(bookLoan.getMaxRenewals())
                .notes(bookLoan.getNotes())
                .isOverdue(bookLoan.getIsOverdue())
                .overdueDays(bookLoan.getOverdueDays())
                .fineAmount(bookLoan.getFine())
                .createdAt(bookLoan.getCreatedAt())
                .updatedAt(bookLoan.getUpdatedAt())
                .build();
    }

}
