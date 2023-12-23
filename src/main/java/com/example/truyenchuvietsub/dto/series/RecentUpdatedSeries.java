package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.ChapterDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.Genre;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class RecentUpdatedSeries {
    private String id;
    private String title;
    private String slug;
    private UserDTO author;
    private String cover;
    private Set<Genre> genres;
    private String chapterId;
    private String chapterTitle;
    private int chapterNumber;
    private Date createdAt;
    private Date updatedAt;
}
