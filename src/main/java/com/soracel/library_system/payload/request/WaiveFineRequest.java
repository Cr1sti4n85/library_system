package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaiveFineRequest {

    @NotNull(message = "El id de la multa es obligatorio")
    private Long fineId;

    @NotNull(message = "La razón de la exención es obligatoria")
    private String reason;
}
