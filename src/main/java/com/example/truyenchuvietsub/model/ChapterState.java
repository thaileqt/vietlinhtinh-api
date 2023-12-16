package com.example.truyenchuvietsub.model;

import com.example.truyenchuvietsub.model.enums.EnumChapterState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chapter_states")
@Getter
@Setter
public class ChapterState {
    @Id
    private String id;
    private EnumChapterState name;

}
