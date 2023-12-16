package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Genre;
import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.model.SeriesState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class SeriesDTO {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String cover;
    private Set<String> genres;
    private UserDTO author;
    private String seriesState;
    private Date createdDate;
    private Date updatedDate;
    private int view;
    private int totalChapter;
    private int totalView;
    private int totalLike;
    private List<ReviewDTO> reviews;


    public static SeriesDTO from(Series series, int totalChapter, int totalView, int totalLike, List<ReviewDTO> reviews) {
        // convert Set<Genre> to Set<String>
        Set<String> genreStr = new HashSet<>();
        Set<Genre> genres = series.getGenres();
        for (Genre genre : genres) {
            genreStr.add(String.valueOf(genre.getName()));
        }
        return SeriesDTO.builder()
                .id(series.getId())
                .title(series.getTitle())
                .slug(series.getSlug())
                .description(series.getDescription())
                .cover(series.getCover())
                .genres(genreStr)
                .author(UserDTO.from(series.getAuthor()))
                .seriesState(String.valueOf(series.getSeriesState().getName()))
                .createdDate(series.getCreatedAt())
                .updatedDate(series.getUpdatedAt())
                .totalChapter(totalChapter)
                .totalView(totalView)
                .totalLike(totalLike)
                .reviews(reviews)
                .build();
    }
}
