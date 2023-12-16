package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.SeriesState;
import com.example.truyenchuvietsub.model.enums.EnumSeriesState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesStateRepository extends MongoRepository<SeriesState, String> {
    Optional<SeriesState> findByName(String name);
    boolean existsByName(EnumSeriesState name);

}
