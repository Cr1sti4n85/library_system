package com.soracel.library_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del género es obligatorio")
    private String code;

    @NotBlank(message = "El nombre del género es obligatorio")
    private String name;

    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String description;

    @Min(value = 0, message = "Valor no puede ser negativo")
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "parent_genre_id")
    private Genre parentGenre;

    @OneToMany(mappedBy = "parentGenre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Genre> subGenres = new ArrayList<>();

//    @OneToMany(mappedBy = "genre",  cascade = CascadeType.PERSIST)
//    private List<Book> books = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
