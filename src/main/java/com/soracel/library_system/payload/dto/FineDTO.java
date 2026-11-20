package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.FineType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class FineDTO {

    private Long id;

    @NotNull(message = "El id del préstamo es obligatorio.")
    private Long bookLoanId;

    @NotNull(message = "El id de usuario es obligatorio.")
    private Long userId;

    private Long paymentId;

    private String bookTitle;

    private String bookIsbn;

    private String username;

    @Email(message = "El correo no tiene formato correcto.")
    private String userEmail;

    @NotNull(message = "El tipo de multa es obligatorio.")
    private FineType type;

    @NotNull(message = "El valor de la multa os obligatorio")
    private BigDecimal amount;

}
