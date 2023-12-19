package com.example.truyenchuvietsub.model;//package com.bezkoder.spring.security.mongodb.models;
//
import com.example.truyenchuvietsub.SlugGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

@Document(collection = "series")
@Setter
@Getter
public class Series {
    @Id
    private String id;
    private String title;
    private String slug;
    private String description;
    @DBRef
    private User author;
    private String cover;
    @DBRef
    private SeriesState seriesState;
    @DBRef
    private Set<Genre> genres = new HashSet<>();
    private int view = 0;
    private Date createdAt = new Date();
    private Date updatedAt;
    private int totalLike;
    private List<ContentWarning> contentWarnings = new ArrayList<>();

    public Series(String title, String description, User author, String cover, SeriesState seriesState) {
        this.title = title;
        this.slug = SlugGenerator.toSlug(title);
        this.description = description;
        this.author = author;
        this.cover = cover;
        this.seriesState = seriesState;
    }

}
