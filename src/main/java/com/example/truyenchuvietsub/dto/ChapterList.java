package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.ChapterState;
import lombok.Data;

import java.util.Date;

@Data
public class ChapterList {
    private String id;
    private String title;
    private Date createdAt;
    private Integer chapterNumber;
    private ChapterState chapterState;
}
