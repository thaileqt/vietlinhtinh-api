package com.example.truyenchuvietsub.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reply_messages")
@Data
@NoArgsConstructor
public class ReplyMessage {
    @Id
    private String id;
    @NonNull
    private String content;
    @DBRef
    private User sender;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
}
