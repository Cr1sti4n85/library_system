package com.soracel.library_system.controller;


import com.soracel.library_system.exception.GenreException;
import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;
import com.soracel.library_system.service.GenreService;
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

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@RequestParam long genreId) throws GenreException {
        GenreDTO genre = genreService.getGenreById(genreId);
        return ResponseEntity.status(HttpStatus.OK).body(genre);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Long genreId,
                                                @RequestBody GenreDTO genre) throws GenreException {

        GenreDTO updatedGenre = genreService.updateGenre(genreId, genre);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<GenreDTO> deleteGenre(@PathVariable Long genreId) throws GenreException {
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<GenreDTO> hardDeleteGenre(@PathVariable Long genreId) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDTO>> getTopLevel() throws GenreException {
        List<GenreDTO> topLevelGenres = genreService.getTopLevelGenres();
        return ResponseEntity.ok(topLevelGenres);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalActiveGenres() throws GenreException {
        Long total = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}/book-count")
    public ResponseEntity<Long> getBookCountByGenre(@PathVariable Long id) throws GenreException {
        Long count = genreService.getBookCountByGenre(id);
        return ResponseEntity.ok(count);
    }

}
