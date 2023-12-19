package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.ChapterDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
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
    private Set<String> genres;
    private ChapterDTO newestChapter;
    private Date updatedAt;
}
