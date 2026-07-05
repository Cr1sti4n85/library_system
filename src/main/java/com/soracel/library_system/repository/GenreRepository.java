package com.soracel.library_system.repository;

import com.soracel.library_system.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GenreRepository extends JpaRepository<Genre, Long> {

}
