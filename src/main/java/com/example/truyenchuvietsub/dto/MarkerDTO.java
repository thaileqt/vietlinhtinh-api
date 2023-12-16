package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Marker;
import lombok.Data;

@Data
public class MarkerDTO {
    private String id;
    private String chapterId;
    private String userId;
    private int paragraphIndex;

    public static MarkerDTO from(Marker marker) {
        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setId(marker.getId());
        markerDTO.setParagraphIndex(marker.getParagraphIndex());
        markerDTO.setChapterId(marker.getChapter().getId());
        markerDTO.setUserId(marker.getUser().getId());

        return markerDTO;
    }
}
