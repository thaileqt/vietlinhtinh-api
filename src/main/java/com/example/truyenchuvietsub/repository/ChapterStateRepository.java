package com.example.truyenchuvietsub.repository;


import com.example.truyenchuvietsub.model.ChapterState;
import com.example.truyenchuvietsub.model.enums.EnumChapterState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChapterStateRepository extends MongoRepository<ChapterState, String> {
    Optional<ChapterState> findByName(String name);
    boolean existsByName(EnumChapterState name);
}
