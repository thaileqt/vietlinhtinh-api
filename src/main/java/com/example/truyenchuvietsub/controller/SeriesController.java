package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.SeriesDTO;
import com.example.truyenchuvietsub.dto.series.*;
import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.service.SeriesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/series")
public class SeriesController {
    @Autowired
    private SeriesService seriesService;


    @GetMapping("/count-user-owned-series/{username}")
    public ResponseEntity<Integer> countOwnedseries(@PathVariable String username) {
        int count = seriesService.countOwnedSeries(username);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SeriesDetail>> searchBySlug(
            // set min length of keyword to 3
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<SeriesDetail> series = seriesService.search(keyword, page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }
    @GetMapping("/get-by-slug/{slug}")
    public ResponseEntity<SeriesDetail> getSeriesBySlug(@PathVariable String slug) {
        SeriesDetail Series = seriesService.getSeriesBySlug(slug);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }


    @GetMapping("/get-by-genre/{genre}")
    public ResponseEntity<List<SeriesDetail>> getseriesByGenre(@PathVariable String genre,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        List<SeriesDetail> series = seriesService.getSeriesByGenre(genre, page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/get-recent-updated-series")
    public ResponseEntity<List<RecentUpdatedSeries>> getTopRecentUpdatedSeries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<RecentUpdatedSeries> series = seriesService.getTopRecentUpdatedSeries(page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/get-recent-created-series")
    public ResponseEntity<List<RecentCreatedSeries>> getTopRecentCreatedSeries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RecentCreatedSeries> series = seriesService.getTopRecentCreatedSeries(page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }


    @GetMapping("/get-hot-series/{seriesCount}")
    public ResponseEntity<List<HotSeries>> getTopHotSeries(@PathVariable int seriesCount) {
        List<HotSeries> series = seriesService.getTopSeriesWithHighestLikes(seriesCount);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<SeriesDTO> createSeries(@RequestBody SeriesDTO createSeriesRequest) {
        SeriesDTO newSeries = seriesService.createSeries(createSeriesRequest);
        return new ResponseEntity<>(newSeries, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<Series> deleteById(@PathVariable String id, Authentication authentication) {
        seriesService.deleteSeriesById(id, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_AUTHOR')")
    @PutMapping("/update-by-slug/{slug}")
    public ResponseEntity<Series> updateSeriesBySlug(@PathVariable String slug, @Valid @RequestBody SeriesDTO seriesDTO) {
        Series updatedSeries = seriesService.updateSeriesBySlug(slug, seriesDTO);
        return new ResponseEntity<>(updatedSeries, HttpStatus.OK);
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<List<UserOwnedSeriesDTO>> getSeriesByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(seriesService.getSeriesByUsername(username, page, size), HttpStatus.OK);
    }

}
