package com.soracel.library_system.service.impl;

import com.soracel.library_system.mapper.GenreMapper;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.repository.GenreRepository;
import com.soracel.library_system.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreDTO createGenre(GenreDTO genreDto) {

        Genre newGenre = GenreMapper.toEntity(genreDto);

        if (genreDto.getParentGenreId() != null) {
            Genre parentGenre = genreRepository.findById(
                    genreDto.getParentGenreId()).orElseThrow();
            newGenre.setParentGenre(parentGenre);
        }
        Genre savedGenre = genreRepository.save(newGenre);
        return GenreMapper.toDTO(savedGenre);
    }



}
