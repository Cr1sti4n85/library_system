package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.request.BookSearchRequest;
import com.soracel.library_system.payload.response.PageResponse;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    List<BookDTO> createBooksBulk(List<BookDTO> bookDTOList);
    BookDTO updateBook(BookDTO bookDTO);
    BookDTO getBookById(Long bookId);
    BookDTO getBookByISBN(String isbn);
    BookDTO updateBook(Long bookId, BookDTO bookDTO);
    void deleteBook(Long bookId);
    void hardDeleteBook(Long bookId);

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest searchRequest
    );

    Long getTotalActiveBooks();

    Long getTotalAvailableBooks();


}
