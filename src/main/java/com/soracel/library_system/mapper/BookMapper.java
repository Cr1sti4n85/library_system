package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.BookDTO;
import com.soracel.library_system.payload.dto.GenreDTO;

public class BookMapper {

    public static BookDTO toDTO(Book book){
        if (book == null){
            return null;
        }

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genreId(book.getGenre().getId())
                .genreName(book.getGenre().getName())
                .genreCode(book.getGenre().getCode())
                .publisher(book.getPublisher())
                .publicationDate(book.getPublishDate())
                .language(book.getLanguage())
                .pages(book.getPages())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .price(book.getPrice())
                .coverimageUrl(book.getCoverImageUrl())
                .active(book.getActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static Book toEntity(BookDTO bookDTO) {

        if  (bookDTO == null){
            return null;
        }

        return Book.builder()
                .id(bookDTO.getId())
                .isbn(bookDTO.getIsbn())
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .publisher(bookDTO.getPublisher())
                .publishDate(bookDTO.getPublicationDate())
                .language(bookDTO.getLanguage())
                .pages(bookDTO.getPages())
                .description(bookDTO.getDescription())
                .totalCopies(bookDTO.getTotalCopies())
                .availableCopies(bookDTO.getAvailableCopies())
                .price(bookDTO.getPrice())
                .coverImageUrl(bookDTO.getCoverimageUrl())
                .active(bookDTO.getActive())
                .build();
    }

    public static void updateEntityFromDTO(BookDTO bookDTO, Book existingBook) {
        if (bookDTO == null || existingBook == null){
            return;
        }
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setPublishDate(bookDTO.getPublicationDate());
        existingBook.setLanguage(bookDTO.getLanguage());
        existingBook.setPages(bookDTO.getPages());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setTotalCopies(bookDTO.getTotalCopies());
        existingBook.setAvailableCopies(bookDTO.getAvailableCopies());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setCoverImageUrl(bookDTO.getCoverimageUrl());

        if (bookDTO.getActive() != null){
            bookDTO.setActive(bookDTO.getActive());
        }

    }
}
