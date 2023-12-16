package com.example.truyenchuvietsub.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
public class Review {
    @Id
    private String id;
    @DBRef
    private Series series;
    @DBRef
    private User user;
    private String content;
    private int rating;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
}
