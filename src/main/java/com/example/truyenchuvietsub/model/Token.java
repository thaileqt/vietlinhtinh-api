package com.example.truyenchuvietsub.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Document("tokens")
@Data
public class Token {
    @Id
    private String id;

    private String userId;
    private String tokenType;
    private String tokenValue;
    private Instant expirationTime;

}
