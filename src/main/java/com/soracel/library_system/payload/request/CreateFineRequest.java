package com.soracel.library_system.payload.request;

import com.soracel.library_system.domain.FineType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFineRequest {

    @NotNull(message = "El id del préstamo es obligatorio.")
    private Long id;

    @NotNull(message = "El tipo de multa es obligatorio.")
    private FineType type;

    @NotNull(message = "El valor de la multa es obligatorio")
    @Positive(message = "El valor de la multa debe ser positivo")
    private BigDecimal amount;

    private String reason;

    private String notes;
}
