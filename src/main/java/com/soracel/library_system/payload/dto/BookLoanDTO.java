package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.BookLoanStatus;
import com.soracel.library_system.domain.BookLoanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookCoverImage;
    @NotNull(message = "El campo es obligatorio")
    private BookLoanType loanType;
    @NotNull(message = "El campo es obligatorio")
    private BookLoanStatus status;

    @NotNull(message = "El campo es obligatorio")
    private LocalDate checkoutDate;

    private LocalDate dueDate;
    private Long remainingDays;

    @NotNull(message = "El campo es obligatorio")
    private LocalDate returnDate;
    @PositiveOrZero(message = "El valor de renovaciones no puede ser menor  cero")
    private Integer renewalCount;
    private Integer maxRenewals;

    @DecimalMin(value = "0.0", inclusive = true, message = "El valor no puede ser negativo")
    @Digits(integer = 6, fraction = 2, message = "La multa debe tener un máximo de 6 dígitos enteros y 2 decimales")
    private BigDecimal fineAmount;
    private Boolean idFinePaid;
    private Boolean isOverdue;
    @Positive(message = "EL valor de días debe ser número positivo")
    private Integer overdueDays;
    @Size(min = 5, max = 150, message = "Las notas deben contener entre y 150 caracteres")
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
