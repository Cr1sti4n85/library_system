package com.soracel.library_system.payload.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenreDTO {
    private Long id;

    @NotBlank(message = "El código del género es obligatorio")
    private String code;

    @NotBlank(message = "El nombre del género es obligatorio")
    private String name;

    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String description;

    @Min(value = 0, message = "Valor no puede ser negativo")
    private Integer displayOrder = 0;

    private Boolean active;

    private Long parentGenreId;

    private String parentGenreName;

    private List<GenreDTO> subGenres;

    private Long bookCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
