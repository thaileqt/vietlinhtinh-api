package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.SeriesDTO;
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


    @GetMapping("/get-user-owned-series/{username}")
    public ResponseEntity<List<SeriesDTO>> getOwnedseries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String username) {
        List<SeriesDTO> series = seriesService.getOwnedSeries(page, size, username);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/count-user-owned-series/{username}")
    public ResponseEntity<Integer> countOwnedseries(@PathVariable String username) {
        int count = seriesService.countOwnedSeries(username);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SeriesDTO> getSeriesBySlug(@PathVariable String slug) {
        Optional<SeriesDTO> Series = seriesService.getSeriesBySlug(slug);
        return Series.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search?keyword={keyword}")
    public ResponseEntity<List<SeriesDTO>> searchBySlug(@RequestParam String keyword) {
        List<SeriesDTO> series = seriesService.searchBySlug(keyword);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/get-by-genre/{genre}")
    public ResponseEntity<List<SeriesDTO>> getseriesByGenre(@PathVariable String genre) {
        List<SeriesDTO> series = seriesService.getSeriesByGenre(genre);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/get-recent-updated-series")
    public ResponseEntity<List<SeriesDTO>> getTopRecentUpdatedSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SeriesDTO> series = seriesService.getTopRecentUpdatedSeries(page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/get-recent-created-series")
    public ResponseEntity<List<SeriesDTO>> getTopRecentCreatedSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SeriesDTO> series = seriesService.getTopRecentCreatedSeries(page, size);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }


    @GetMapping("/get-hot-series/{seriesCount}")
    public ResponseEntity<List<SeriesDTO>> getTopHotSeries(@PathVariable int seriesCount) {
        List<SeriesDTO> series = seriesService.getTopSeriesWithHighestLikes(seriesCount);
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


}
