package com.soracel.library_system.payload.request;

import com.soracel.library_system.domain.BookLoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanSearchRequest {
    private Long userId;
    private Long bookId;
    private BookLoanStatus status;
    private Boolean overdueOnly;
    private Boolean unpaidFinesOnly;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String  sortDirection;
}
