package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.UserDTO;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class NewestSeries {
    private String id;
    private String title;
    private String slug;
    private UserDTO author;
    private String cover;
}
