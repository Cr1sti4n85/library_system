package com.soracel.library_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Genre genre;

    private String publisher;

    private LocalDate publishDate;

    private String language;

    private Integer pages;

    private String description;

    @Column(nullable = false)
    private Integer totalCopies;

    private Integer availableCopies;

    private BigDecimal price;

    private String coverImageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDate updatedAt;

    @AssertTrue(message = "Las copias disponibles no pueden exceder el total de copias")
    public boolean isAvailableCopiesValid() {
        if (this.totalCopies == null || this.availableCopies == null) {
            return true;
        }
        return this.availableCopies <= this.totalCopies;
    }

}

