package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.model.SeriesState;
import com.example.truyenchuvietsub.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
public class UserOwnedSeriesDTO {
    private String id;
    private String title;
    private String slug;
    private UserDTO author;
    private String description;
    private String cover;
    private List<Genre> genres;
    private SeriesState seriesState;
    private Date createdAt;
    private Date updatedAt;
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalChapters;
    private Integer totalReviews;
    // average rating
    private Double averageRating;


}
