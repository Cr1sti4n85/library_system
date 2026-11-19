package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "EL id del libro es obligatorio")
    private Long bookId;

    private String notes;
}
