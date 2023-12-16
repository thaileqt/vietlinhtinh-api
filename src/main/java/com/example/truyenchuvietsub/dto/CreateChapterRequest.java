package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Chapter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class CreateChapterRequest {
    private String title;
    private String content;
    private String seriesSlug;
    private String chapterStatus;
}
