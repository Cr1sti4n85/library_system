package com.soracel.library_system.payload.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReviewDTO {

    private Long id;

    @NotNull
    private Long userId;

    private String userName;

    @NotNull
    private long bookId;

    private String bookTitle;

    @Min(value = 1, message = "El valor mínimo del rating debe ser 1")
    @Max(value = 5, message = "El valor máximo del rating debe ser 5")
    private Integer rating;

    @NotBlank
    @Size(min = 5, max = 250, message = "El mensaje debe contener entre 5 y 250 caracteres")
    @NotNull(message = "El mensaje es obligatorio")
    private String reviewText;

    @Size(max = 20, message = "El título no debe exceder los 20 caracteres")
    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
