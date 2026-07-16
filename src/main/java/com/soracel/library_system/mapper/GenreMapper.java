package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;

public class GenreMapper {
    public static GenreDTO toDTO(Genre genre){
        if (genre == null){
            return null;
        }
        GenreDTO genreDto = GenreDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .code(genre.getCode())
                .displayOrder(genre.getDisplayOrder())
                .active(genre.getActive())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();

        if (genre.getParentGenre() != null) {
            genreDto.setParentGenreId(genre.getParentGenre().getId());
            genreDto.setParentGenreName(genre.getParentGenre().getName());
        }

        if (genre.getSubGenres() != null && !genre.getSubGenres().isEmpty()) {
            genreDto.setSubGenres(genre.getSubGenres()
                    .stream()
                    .filter(Genre::getActive)
                    .map(GenreMapper::toDTO).toList());
        }

        return  genreDto;
    }

    public static Genre toEntity(GenreDTO genreDTO) {
        if (genreDTO == null){
            return null;
        }
        return Genre.builder()
                .name(genreDTO.getName())
                .code(genreDTO.getCode())
                .description(genreDTO.getDescription())
                .displayOrder(genreDTO.getDisplayOrder())
                .active(true)
                .build();
    }
}
