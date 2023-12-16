package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @GetMapping("/is-chapter-liked/{chapterId}")
    public boolean isLiked(@PathVariable  String chapterId, Authentication authentication) {
        return likeService.isChapterLiked(chapterId, authentication);
    }

    @PostMapping("/like-chapter/{chapterId}")
    public ResponseEntity like(@PathVariable String chapterId, Authentication authentication) {
        likeService.likeChapter(chapterId, authentication);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlike-chapter/{chapterId}")
    public ResponseEntity unlike(@PathVariable  String chapterId, Authentication authentication) {
        likeService.unlikeChapter(chapterId, authentication);
        return ResponseEntity.ok().build();
    }
}
