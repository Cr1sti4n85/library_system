package com.soracel.library_system.payload.request;

import com.soracel.library_system.domain.BookLoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckinRequest {
    @NotNull(message = "El campo id es obligatorio")
    private  Long bookLoanId;

    private BookLoanStatus status;

    private String notes;
}
