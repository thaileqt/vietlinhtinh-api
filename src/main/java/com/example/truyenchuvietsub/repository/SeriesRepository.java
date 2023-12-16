package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.model.Series;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends MongoRepository<Series, String> {
    List<Series> findAllByAuthor_Id(String id);
    Optional<Series> findBySlug(String slug);

    void deleteBySlug(String slug);

    List<Series> findSeriesByGenres(Genre genre);

    List<Series> findAllBySlugContaining(String keyword);


}
