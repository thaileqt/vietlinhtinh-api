package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.Marker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkerRepository extends MongoRepository<Marker, String> {
    Optional<Marker> findByChapter_IdAndUser_Id(String chapterId, String userId);
    Boolean existsByChapter_IdAndUser_IdAndParagraphIndex(String chapterId, String userId, Integer paragraphIndex);
    Boolean existsByChapter_IdAndUser_Id(String chapterId, String userId);

}
