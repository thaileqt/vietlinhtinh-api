package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String>{
    List<Comment> findAllByUser_Id(String userId);
    void deleteAllByChapter_Id(String chapterId);
}
