package com.soracel.library_system.service;

import com.soracel.library_system.exception.GenreException;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {

    GenreDTO createGenre(GenreDTO genre);

    List<GenreDTO> getAllGenres();

    GenreDTO getGenreById(Long id) throws GenreException;

    GenreDTO updateGenre(Long id, GenreDTO genre);

    void deleteGenre(Long id);

    void hardDeleteGenre(Long id);

    List<GenreDTO> getAllActiveGenresWithSubgenres();

    List<GenreDTO> getTopLevelGenres();

    //Page<GenreDTO> searchGenres(String searchTerm, Pageable pageable);

    Long getTotalActiveGenres();

    Long getBookCountByGenre(Long genreId);
}
