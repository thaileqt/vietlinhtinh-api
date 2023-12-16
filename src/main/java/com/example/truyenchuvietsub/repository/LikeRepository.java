package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Like;
import com.example.truyenchuvietsub.model.Series;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    boolean existsByChapter_IdAndUser_Id(String chapterId, String userId);
    void deleteByChapter_IdAndUser_Id(String chapterId, String userId);
    List<Like> findAllByChapter_Series_Id(String seriesId);
    List<Like> findAllByChapter_Series(Series series);
    List<Like> findAllByChapter_Id(String chapterId);
    List<Like> findAllByUser_Id(String userId);

}
