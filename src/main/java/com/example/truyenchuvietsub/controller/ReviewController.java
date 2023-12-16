package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    // get all reviews of a Series
    @GetMapping("/get-by-series-id/{seriesId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsBySeries(@PathVariable String seriesId) {
        return ResponseEntity.ok(reviewService.getReviewsBySeries(seriesId));
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO, authentication));
    }

    @PutMapping("/update")
    public ResponseEntity<ReviewDTO> updateReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        return ResponseEntity.ok(reviewService.updateReview(reviewDTO, authentication));
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id, Authentication authentication) {
        reviewService.deleteReview(id, authentication);
        return ResponseEntity.ok().build();
    }
}
