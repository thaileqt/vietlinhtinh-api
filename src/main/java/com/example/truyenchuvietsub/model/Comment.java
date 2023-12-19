package com.example.truyenchuvietsub.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "comments")
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    @NonNull
    private String content;
    @DBRef
    private User user;
    @DBRef
    private Chapter chapter;
    private Date createdAt = new Date();
}
