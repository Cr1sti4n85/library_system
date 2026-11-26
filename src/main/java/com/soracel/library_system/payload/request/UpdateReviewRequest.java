package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewRequest {

    @Min(value = 1, message = "El valor mínimo del rating debe ser 1")
    @Max(value = 5, message = "El valor máximo del rating debe ser 5")
    private Integer rating;

    @NotBlank
    @Size(min = 5, max = 250, message = "El mensaje debe contener entre 5 y 250 caracteres")
    @NotNull(message = "El mensaje es obligatorio")
    private String reviewText;

    @Size(max = 20, message = "El título no debe exceder los 20 caracteres")
    private String title;
}
