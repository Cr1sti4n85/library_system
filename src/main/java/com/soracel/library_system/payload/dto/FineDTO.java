package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class FineDTO {

    private Long id;

    @NotNull(message = "El id del préstamo es obligatorio.")
    private Long bookLoanId;

    @NotNull(message = "El id de usuario es obligatorio.")
    private Long userId;

    private String paymentIntentId;

    private String bookTitle;

    private String bookIsbn;

    private String username;

    @Email(message = "El correo no tiene formato correcto.")
    private String userEmail;

    @NotNull(message = "El tipo de multa es obligatorio.")
    private FineType type;

    @NotNull(message = "El valor de la multa os obligatorio")
    private BigDecimal amount;

    @NotNull(message = "El status el obligatorio")
    private FineStatus status;

    private String reason;

    private String notes;

    private Long waivedByUserId;

    private String waivedByUsername;

    private LocalDateTime waivedAt;

    private String waiverReason;

    private LocalDateTime paidAt;

    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
