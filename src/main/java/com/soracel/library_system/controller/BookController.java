package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.request.BookSearchRequest;
import com.soracel.library_system.payload.response.BookStatsResponse;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BookDTO>> createBooks(@Valid @RequestBody List<BookDTO> booksDTO) {
        List<BookDTO> createdBooks = bookService.createBooksBulk(booksDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<BookDTO> deleteBookPermanently(@PathVariable Long id) {
        bookService.hardDeleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(@RequestBody BookSearchRequest searchRequest){
        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats(){
        long totalActive = bookService.getTotalActiveBooks();
        long totalAvailable = bookService.getTotalAvailableBooks();

        BookStatsResponse stats = BookStatsResponse.builder()
                .totalActive(totalActive)
                .totalAvailable(totalAvailable)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

}
