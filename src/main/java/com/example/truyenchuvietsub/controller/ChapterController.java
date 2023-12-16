package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.ChapterDTO;
import com.example.truyenchuvietsub.dto.CreateChapterRequest;
import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.service.ChapterService;
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
@RequestMapping("/api/chapters")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{id}")
    public ResponseEntity<ChapterDTO> getChapterById(@PathVariable("id") String id) {
        Optional<ChapterDTO> chapter = chapterService.singleChapter(id);
        return chapter.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/get-all-by-series-id-for-navigation/{id}")
    public ResponseEntity<List<ChapterDTO>> getAllChaptersBySeriesId(@PathVariable("id") String id) {
        List<ChapterDTO> chapterDTOs = chapterService.getAllChaptersBySeriesId(id);
        return new ResponseEntity<>(chapterDTOs, HttpStatus.OK);
    }

    @GetMapping("/get-by-series-slug/{seriesSlug}")
    public ResponseEntity<List<ChapterDTO>> getChaptersBySeriesId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @PathVariable String seriesSlug) {
        List<ChapterDTO> chapterDTOs = chapterService.getChaptersBySeriesSlug(seriesSlug, page, size);
        return new ResponseEntity<>(chapterDTOs, HttpStatus.OK);
    }

    @GetMapping("/get-by-series-and-chapter/{seriesSlug}/{chapterNumber}")
    public ResponseEntity<ChapterDTO> getChapterBySeriesSlugAndChapterNumber(@PathVariable String seriesSlug, @PathVariable int chapterNumber) {
        ChapterDTO chapterDTO = chapterService.getChapterBySeriesSlugAndChapterNumber(seriesSlug, chapterNumber);
        return new ResponseEntity<>(chapterDTO, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ChapterDTO> createChapter(@Valid @RequestBody CreateChapterRequest createChapterRequest, Authentication authentication) {
        ChapterDTO createdChapter = chapterService.createChapter(createChapterRequest, authentication);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    // check if user is already logged in and has the role of admin
    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<Chapter> deleteChapter(@PathVariable("id") String id) {
        chapterService.deleteChapterById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update-by-series-and-chapter/{seriesSlug}/{chapterNumber}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String seriesSlug, @PathVariable int chapterNumber, @RequestBody ChapterDTO editChapterRequest) {
        Chapter chapter = chapterService.updateChapterByChapterNumberAndSeriesSlug(seriesSlug, chapterNumber, editChapterRequest);
        return new ResponseEntity<>(chapter, HttpStatus.OK);
    }

    @GetMapping("/count-by-series-slug/{seriesSlug}")
    public ResponseEntity<Integer> countChapterBySeriesSlug(@PathVariable String seriesSlug) {
        int count = chapterService.countChaptersBySeries_Slug(seriesSlug);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

}
