package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    @Autowired
    private GenreRepository genreRepository;

    @GetMapping
    public ResponseEntity<List<Genre>> getGenres() {
        List<Genre> genres = genreRepository.findAll();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Genre> addGenre(Genre genre) {
        Genre newGenre = genreRepository.save(genre);
        return new ResponseEntity<>(newGenre, HttpStatus.CREATED);
    }
}
