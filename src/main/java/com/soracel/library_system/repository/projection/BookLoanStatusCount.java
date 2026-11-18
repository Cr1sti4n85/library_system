package com.soracel.library_system.repository.projection;

import com.soracel.library_system.domain.BookLoanStatus;

public interface BookLoanStatusCount {

    BookLoanStatus getStatus();

    Long getTotal();
}
