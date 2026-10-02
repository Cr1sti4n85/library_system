package com.soracel.library_system.payload.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {

    private Long id;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 255, message = "El título debe contener entre 1 y 255 caracteres")
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre del autor debe contener entre 1 y 255 caracteres")
    private String author;

    @NotNull(message = "El género es obligatorio")
    private Long genreId;

    private String genreName;

    private String genreCode;

    @Size(max = 100, message = "El nombre de la editorial no debe exceder los cien caracteres")
    private String publisher;

    private LocalDate publicationDate;

    @Size(max = 20, message = "El nombre del idioma no debe exceder 20 caracteres")
    private String language;

    @Min(value = 2, message = "La cantidad debe ser mayor a 1")
    @Max(value = 5000, message = "La cantidad de páginas no debe exceder 5000")
    private Integer pages;

    @Size(max = 2000, message = "La descripción no debe exceder los 2000 caracteres")
    private String description;

    @Min(value = 0, message = "el total de copias no puede ser negativo")
    @NotNull(message = "El total de copias es obligatorio")
    private Integer totalCopies;

    @Min(value = 0, message = "La cantidad de copias disponibles no puede ser negativa")
    private Integer availableCopies;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener un máximo de 8 dígitos enteros y 2 decimales")
    private BigDecimal price;

    @Size(max = 500, message = "La URL de la imagen no debe exceder los 500 caracteres")
    private String coverimageUrl;

    private Boolean alreadyBorrowed;

    private Boolean alreadyReserved;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
