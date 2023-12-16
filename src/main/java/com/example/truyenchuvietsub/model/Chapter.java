package com.example.truyenchuvietsub.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "chapters")
@Getter
@Setter
public class Chapter {
    @Id
    private String id;
    @DBRef
    private Series series;
    private String title;
    private String content;
    private int chapterNumber;
    @DBRef
    private ChapterState chapterState;
    private Date createdAt;
    private Date updatedAt;
    private int viewCount;

    public Chapter(Series series, String title, String content, int chapterNumber) {
        this.series = series;
        this.title = title;
        this.content = content;
        this.chapterNumber = chapterNumber;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
