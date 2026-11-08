package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.service.BookService;
import com.soracel.library_system.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final BookService bookService;
    private final GenreService genreService;

    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PostMapping("/books/bulk")
    public ResponseEntity<List<BookDTO>> createBooks(@Valid @RequestBody List<BookDTO> booksDTO) {
        List<BookDTO> createdBooks = bookService.createBooksBulk(booksDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/books/{id}/permanent")
    public ResponseEntity<BookDTO> deleteBookPermanently(@PathVariable Long id) {
        bookService.hardDeleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/genres")
    public ResponseEntity<GenreDTO> addGenre(@Valid @RequestBody GenreDTO genre) {
        GenreDTO createdGenre = genreService.createGenre(genre);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @PutMapping("/genres/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Long genreId,
                                                @Valid @RequestBody GenreDTO genre) {

        GenreDTO updatedGenre = genreService.updateGenre(genreId, genre);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
    }

    @DeleteMapping("/genres/{genreId}")
    public ResponseEntity<GenreDTO> deleteGenre(@PathVariable Long genreId) {
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/genres/{genreId}/permanent")
    public ResponseEntity<GenreDTO> hardDeleteGenre(@PathVariable Long genreId) {
        genreService.hardDeleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }
}
