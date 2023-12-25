package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.model.Review;
import com.example.truyenchuvietsub.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    boolean existsBySeriesAndUser(Series series, User user);
    List<Review> findAllBySeries_Id(String SeriesId);

    void deleteAllBySeries_Id(String SeriesId);

}
