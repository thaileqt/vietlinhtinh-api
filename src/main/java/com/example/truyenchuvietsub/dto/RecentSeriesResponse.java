package com.example.truyenchuvietsub.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class RecentSeriesResponse {
    private String id;
    private String title;
    private String slug;
    private String cover;
    private Date updatedDate;
    private int chapterCount;

    public static RecentSeriesResponse from(String id, String title, String slug, String cover, Date updatedDate, int chapterCount) {
        return RecentSeriesResponse.builder()
                .id(id)
                .title(title)
                .slug(slug)
                .cover(cover)
                .updatedDate(updatedDate)
                .chapterCount(chapterCount)
                .build();
    }
}
