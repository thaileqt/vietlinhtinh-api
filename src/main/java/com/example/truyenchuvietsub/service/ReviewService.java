package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.model.Review;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.SeriesRepository;
import com.example.truyenchuvietsub.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private UserManager userManager;

    public List<ReviewDTO> getReviewsBySeries(String SeriesId) {
        return reviewRepository.findAllBySeries_Id(SeriesId).stream()
                .map(ReviewDTO::from)
                .toList();
    }

    public ReviewDTO createReview(ReviewDTO reviewDTO, Authentication authentication) {
        User user = (User) userManager.loadUserByUsername(authentication.getName());
        Series series = seriesRepository.findById(reviewDTO.getSeriesId()).orElseThrow();
        // check if user is not author of this Series
        if (series.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to review your own Series");
        }
        // check if user has already reviewed this Series
        if (reviewRepository.existsBySeriesAndUser(series, user)) {
            throw new RuntimeException("You have already reviewed this Series");
        }
        Review review = new Review();
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setSeries(seriesRepository.findById(reviewDTO.getSeriesId()).orElseThrow());
        review.setUser(user);
        reviewRepository.save(review);
        return ReviewDTO.from(review);
    }

    public ReviewDTO updateReview(ReviewDTO reviewDTO, Authentication authentication) {
        User user = (User) userManager.loadUserByUsername(authentication.getName());
        Review review = reviewRepository.findById(reviewDTO.getId()).orElseThrow();
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to update this review");
        }
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        reviewRepository.save(review);
        return ReviewDTO.from(review);
    }

    public void deleteReview(String id, Authentication authentication) {
        User user = (User) userManager.loadUserByUsername(authentication.getName());
        Review review = reviewRepository.findById(id).orElseThrow();
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this review");
        }
        reviewRepository.deleteById(id);
    }
}
