package com.example.truyenchuvietsub.model;

import com.example.truyenchuvietsub.model.enums.EnumSeriesState;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("series_states")
@Getter
@Setter
public class SeriesState {
    @Id
    private String id;
    private EnumSeriesState name;
    private String shortDescription;
    private String longDescription;
}
