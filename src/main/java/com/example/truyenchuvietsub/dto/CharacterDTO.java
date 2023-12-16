package com.example.truyenchuvietsub.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class CharacterDTO {
    @NonNull
    private String name;
    private String description;
    @NonNull
    private String seriesSlug;
    private String cover;

}
