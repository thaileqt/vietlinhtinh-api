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
public class ChapterDTO {
    private String id;
    private String title;
    private String content;
    private String seriesSlug;
    private int viewCount;
    private int likeCount;
    private int chapterNumber;
    private Date createdAt;
    private Date updatedAt;
    private String chapterState;
    private String seriesId;
    private List<CommentDTO> comments;

    public static ChapterDTO from(Chapter chapter, int likeCount, List<CommentDTO> comments) {
        return ChapterDTO.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .seriesSlug(chapter.getSeries().getSlug())
                .viewCount(chapter.getViewCount())
                .likeCount(likeCount)
                .chapterNumber(chapter.getChapterNumber())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .chapterState(String.valueOf(chapter.getChapterState().getName()))
                .seriesId(chapter.getSeries().getId())
                .comments(comments)
                .build();
    }
}
