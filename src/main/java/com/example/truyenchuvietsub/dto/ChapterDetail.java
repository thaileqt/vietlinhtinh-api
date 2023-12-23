package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.ChapterState;
import com.example.truyenchuvietsub.model.Series;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ChapterDetail {
    private String id;
    private String title;
    private String content;
    private SeriesInfo series;
    private int viewCount;
    private int likeCount;
    private int chapterNumber;
    private String createdAt;
    private String updatedAt;
    private ChapterState chapterState;
    private List<CommentDTO> comments;
}

@Data
class SeriesInfo {
    private String id;
    private String title;
    private String cover;
    private UserDTO author;
}
