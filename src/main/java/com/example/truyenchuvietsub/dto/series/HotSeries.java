package com.example.truyenchuvietsub.dto.series;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class HotSeries {
    private String id;
    private String title;
    private String slug;
    private String cover;
}
