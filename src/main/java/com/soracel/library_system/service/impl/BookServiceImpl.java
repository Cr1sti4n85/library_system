package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.mapper.BookMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.request.BookSearchRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.GenreRepository;
import com.soracel.library_system.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final GenreRepository genreRepository;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book newBook = BookMapper.toEntity(bookDTO);

        if (bookDTO.getGenreId() != null){
            Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(
                    () -> new BookException("Género no encontrado"));
            newBook.setGenre(genre);
        }
        return null;
    }

    @Override
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOList) {
        return List.of();
    }

    @Override
    public BookDTO updateBook(BookDTO bookDTO) {

//        if (bookDTO.getGenreId() != null){
//            Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(
//                    () -> new BookException("Género no encontrado"));
//            newBook.setGenre(genre);
//        }

        return null;
    }

    @Override
    public BookDTO getBookById(Long bookId) {
        return null;
    }

    @Override
    public BookDTO getBookByISBN(String isbn) {
        return null;
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        return null;
    }

    @Override
    public void deleteBook(Long bookId) {

    }

    @Override
    public void hardDeleteBook(Long bookId) {

    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {
        return null;
    }

    @Override
    public Long getTotalActiveBooks() {
        return 0L;
    }

    @Override
    public Long getTotalAvailableBooks() {
        return 0L;
    }
}
