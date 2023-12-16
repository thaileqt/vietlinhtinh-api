package com.example.truyenchuvietsub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "likes")
@Data
public class Like {
    @Id
    private String id;
    @DBRef
    private Chapter chapter;
    @DBRef
    private User user;
    private Date createAt;

    public Like() {
        this.createAt = new Date();
    }
}
