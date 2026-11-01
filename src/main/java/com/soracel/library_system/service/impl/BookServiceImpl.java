package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.BookMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.request.BookSearchRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.GenreRepository;
import com.soracel.library_system.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {

        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BookException("Libro con ISBN " + bookDTO.getIsbn() + " ya existe");
        }
        Book newBook = BookMapper.toEntity(bookDTO);

        if (bookDTO.getGenreId() != null){
            Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(
                    () -> new NotFoundException("Género no encontrado"));
            newBook.setGenre(genre);
        }

        Book savedBook = bookRepository.save(newBook);

        return BookMapper.toDTO(savedBook);
    }

    @Override
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOList) {

        List<BookDTO> createdBooks =  new ArrayList<>();
        for (BookDTO bookDTO : bookDTOList) {
            BookDTO book = createBook(bookDTO);
            createdBooks.add(book);
        }
        return  createdBooks;
    }

    @Override
    public BookDTO getBookById(Long bookId) {

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Libro no encontrado"));
        return BookMapper.toDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookException("No hay resultados para el ISBN ingresado"));

        return BookMapper.toDTO(book);
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));

        //TODO: validate total copies can't be less than the sum of available + borrowed

        BookMapper.updateEntityFromDTO(bookDTO, existingBook);

        if (bookDTO.getGenreId() != null){
            Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(
                    () -> new NotFoundException("Género no encontrado"));
            existingBook.setGenre(genre);
        }
        Book savedBook =  bookRepository.save(existingBook);

        return BookMapper.toDTO(savedBook);
    }

    @Override
    public void deleteBook(Long bookId) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));

        existingBook.setActive(false);
        bookRepository.save(existingBook);
    }

    @Override
    public void hardDeleteBook(Long bookId) {

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));

        bookRepository.delete(existingBook);

    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {
        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getPageSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection());

        Page<Book> bookPage = bookRepository.searchBooksWithFilters(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable
        );

        return convertToPageResponse(bookPage);
    }

    @Override
    public Long getTotalActiveBooks() {

        return bookRepository.countByActiveTrue();
    }

    @Override
    public Long getTotalAvailableBooks() {

        return bookRepository.countByAvailableBooks();
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        size = Math.min(size, 20);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return  PageRequest.of(page, size, sort);
    }

    private PageResponse<BookDTO> convertToPageResponse(Page<Book> bookPage) {
        List<BookDTO> bookDTOs = bookPage.getContent()
                .stream()
                .map(BookMapper::toDTO)
                .toList();

        return new PageResponse<>(
                bookDTOs,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast(),
                bookPage.isFirst(),
                bookPage.isEmpty());
    }
}
