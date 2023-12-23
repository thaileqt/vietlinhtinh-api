package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.ChapterState;
import com.example.truyenchuvietsub.model.Series;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ChapterDetail {
    private String id;
    private String title;
    private String content;
    private SeriesInfo series;
    private Integer viewCount;
    private Integer likeCount;
    private Integer chapterNumber;
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
