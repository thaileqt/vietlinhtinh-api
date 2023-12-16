package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.ReplyMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyMessageRepository extends MongoRepository<ReplyMessage, String> {

}
