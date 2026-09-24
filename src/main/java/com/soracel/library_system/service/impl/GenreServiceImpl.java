package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.GenreException;
import com.soracel.library_system.mapper.GenreMapper;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.repository.GenreRepository;
import com.soracel.library_system.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreDTO createGenre(GenreDTO genreDto) {

        Genre newGenre = GenreMapper.toEntity(genreDto);

        if (genreDto.getParentGenreId() != null) {
            genreRepository.findById(genreDto.getParentGenreId())
                    .ifPresent(genre -> genre.setParentGenre(newGenre));
        }
        Genre savedGenre = genreRepository.save(newGenre);
        return GenreMapper.toDTO(savedGenre);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDTO getGenreById(Long id) throws GenreException {
        Genre genre = genreRepository.findById(id).orElseThrow(
                () -> new GenreException("Género no encontrado")
        );
        return  GenreMapper.toDTO(genre);
    }

    @Override
    public GenreDTO updateGenre(Long id, GenreDTO genreDto) throws GenreException {
        Genre existingGenre = genreRepository.findById(id).orElseThrow(
                () -> new GenreException("Género no encontrado")
        );

        GenreMapper.updateEntityFromDto(genreDto, existingGenre);

        if (genreDto.getParentGenreId() != null) {
            genreRepository.findById(genreDto.getParentGenreId())
                    .ifPresent(existingGenre::setParentGenre);
        }

        Genre updatedGenre = genreRepository.save(existingGenre);

        return GenreMapper.toDTO(updatedGenre);
    }

    @Override
    public void deleteGenre(Long id) throws GenreException {
        Genre existingGenre = genreRepository.findById(id).orElseThrow(
                () -> new GenreException("Género no encontrado")
        );
        existingGenre.setActive(false);
        genreRepository.save(existingGenre);
    }

    @Override
    public void hardDeleteGenre(Long id) throws GenreException {
        Genre existingGenre = genreRepository.findById(id).orElseThrow(
                () -> new GenreException("Género no encontrado")
        );
        genreRepository.delete(existingGenre);
    }

    @Override
    public List<GenreDTO> getAllActiveGenresWithSubgenres() {

        List<Genre> activeGenres = genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return GenreMapper.toDTOList(activeGenres);
    }

    @Override
    public List<GenreDTO> getTopLevelGenres() {

        List<Genre> topLevelGenres = genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
        return GenreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public Long getTotalActiveGenres() {

        return genreRepository.countByActiveTrue();
    }

    @Override
    public Long getBookCountByGenre(Long genreId) {
        return 0L;
    }


}
