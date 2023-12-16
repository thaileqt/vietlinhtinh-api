package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Character;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends MongoRepository<Character, String> {

    List<Character> findAllBySeries_Slug(String seriesSlug);

}
