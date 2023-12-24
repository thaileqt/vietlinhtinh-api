package com.example.truyenchuvietsub.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ChapterList {
    private String id;
    private String title;
    private Date createdAt;
    private Integer chapterNumber;
}
