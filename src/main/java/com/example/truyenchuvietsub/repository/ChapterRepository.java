package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.Series;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {
    List<Chapter> findAllBySeries(Series series);

    Optional<Chapter> findFirstBySeriesOrderByChapterNumber(Series series);
    @Query(value = "{'Series': ?0}", sort = "{'chapterNumber': -1}")
    Optional<Chapter> findHighestChapterNumber(Series series);

    Optional<Chapter> findTopBySeriesOrderByChapterNumberDesc(Series series);

    void deleteAllBySeries(Series series);

    Optional<Chapter> findChapterBySeriesAndChapterNumber(Series series, int chapterNumber);

    // count how many chapters of a Series
    int countChaptersBySeries(Series series);




}
