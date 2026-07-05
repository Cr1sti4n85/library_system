package com.soracel.library_system.service;

import com.soracel.library_system.model.Genre;
import com.soracel.library_system.payload.dto.GenreDTO;

public interface GenreService {

    GenreDTO createGenre(GenreDTO genre);
}
