package com.example.truyenchuvietsub.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "messages")
@Data
@NoArgsConstructor
public class Message {
    @Id
    private String id;
    private String content;
    @DBRef
    private User sender;
    @DBRef
    private User receiver;
    @DBRef
    private List<ReplyMessage> replyMessages = new ArrayList<>();
    private Date createdAt = new Date();
    private Date updatedAt = new Date();

}
