package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenewalRequest {
    @NotNull(message = "El id del préstamo es obligatorio")
    private Long bookLoanId;

    private String notes;
}

