package com.soracel.library_system.controller;


import com.soracel.library_system.exception.GenreException;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genre) {
        GenreDTO createdGenre = genreService.createGenre(genre);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Long id,  @RequestBody GenreDTO genre) throws GenreException {

        GenreDTO updatedGenre = genreService.updateGenre(id, genre);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenreDTO> deleteGenre(@PathVariable Long id) throws GenreException {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
