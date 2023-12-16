package com.example.truyenchuvietsub.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "markers")
@Setter
@Getter
@NoArgsConstructor
public class Marker {
    @Id
    private String id;
    @DBRef
    private User user;
    @DBRef
    private Chapter chapter;
    private int paragraphIndex;
    private Date createdAt = new Date();

    public Marker(Chapter chapter, User user, int paragraphIndex) {
        this.chapter = chapter;
        this.user = user;
        this.paragraphIndex = paragraphIndex;
    }
}
