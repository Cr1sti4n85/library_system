package com.soracel.library_system.controller;


import com.soracel.library_system.exception.GenreException;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable long genreId) {
        GenreDTO genre = genreService.getGenreById(genreId);
        return ResponseEntity.status(HttpStatus.OK).body(genre);
    }

    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDTO>> getTopLevel() {
        List<GenreDTO> topLevelGenres = genreService.getTopLevelGenres();
        return ResponseEntity.ok(topLevelGenres);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalActiveGenres() {
        Long total = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}/book-count")
    public ResponseEntity<Long> getBookCountByGenre(@PathVariable Long id) {
        Long count = genreService.getBookCountByGenre(id);
        return ResponseEntity.ok(count);
    }

}
