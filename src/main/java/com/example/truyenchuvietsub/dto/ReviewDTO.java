package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Review;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReviewDTO {
    private String id;
    private String content;
    private UserDTO user;
    private String seriesId;
    private int rating;
    private Date createdAt;

    public static ReviewDTO from(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .user(UserDTO.from(review.getUser()))
                .seriesId(review.getSeries().getId())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
