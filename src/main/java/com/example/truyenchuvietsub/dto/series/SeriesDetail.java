package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.ChapterDTO;
import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.model.SeriesState;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeriesDetail {
    private String id;
    private String title;
    private String slug;
    private UserDTO author;
    private String description;
    private String cover;
    private List<Genre> genres;
    private SeriesState seriesState;
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalChapters;
    private Integer totalReviews;
    // average rating
    private Double averageRating;
    private List<ReviewDTO> reviews;
    private Date createdAt;
    private Date updatedAt;
}
