package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.model.enums.EnumGenre;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GenreRepository extends MongoRepository<Genre, String> {
    Optional<Genre> findByName(EnumGenre name);
    boolean existsByName(EnumGenre name);
}
