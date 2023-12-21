package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.MarkerDTO;
import com.example.truyenchuvietsub.model.Marker;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/markers")
public class MarkerController {
    @Autowired
    private MarkerService markerService;

    @PostMapping("/create")
    public ResponseEntity<MarkerDTO> createMarker(@RequestBody MarkerDTO markerDTO, Authentication authentication) {
        MarkerDTO createdMarker = markerService.createMarker(markerDTO, authentication);
        return new ResponseEntity<>(createdMarker, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<Marker> deleteMarker(@PathVariable("id") String id) {
        markerService.deleteMarker(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-by-chapter/{chapterId}")
    public ResponseEntity<MarkerDTO> getMarkedParagraphIds(@PathVariable("chapterId") String chapterId, Authentication authentication) {
        MarkerDTO markerDTO = markerService.getUserMarkerInChapter(chapterId, authentication);
        return new ResponseEntity<>(markerDTO, HttpStatus.OK);
    }

    @GetMapping("/get-by-series-slug-and-chapter-number/{seriesSlug}/{chapterNumber}")
    public ResponseEntity<MarkerDTO> getMarkedParagraphIdsBySeriesSlugAndChapterNumber(@PathVariable("seriesSlug") String seriesSlug, @PathVariable("chapterNumber") int chapterNumber, Authentication authentication) {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
