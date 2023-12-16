package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByTokenValue(String tokenValue);
    Optional<Token> findByUserIdAndTokenType(String userId, String tokenType);
    void deleteByTokenValue(String tokenValue);

    void deleteByUserIdAndTokenType(String userId, String tokenType);

    void deleteAllByUserId(String userId);


}
