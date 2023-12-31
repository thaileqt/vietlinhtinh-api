package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.SeriesState;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class HotSeries {
    private String id;
    private String title;
    private String slug;
    private String cover;
    private UserDTO author;
    private SeriesState seriesState;
    private Integer totalLikes;
    private Integer totalViews;
    private Integer totalChapters;
    private Double averageRating;
}
