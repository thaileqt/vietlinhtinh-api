package com.example.truyenchuvietsub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
@Getter
@Setter
@NoArgsConstructor
public class Character {
    @Id
    private String id;
    @NonNull
    private String name;
    private String description;
    private String cover;
    @DBRef
    private Series series;
}
