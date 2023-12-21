package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.Like;
import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserManager userManager;
    @Autowired
    private ChapterService chapterService;

    public boolean isChapterLiked(String chapterId, Authentication authentication) {
        if (authentication == null) {
            System.out.println("null");
            return false;
        }
        String userId = ((User) authentication.getPrincipal()).getId();
        return likeRepository.existsByChapter_IdAndUser_Id(chapterId, userId);
    }

    public void likeChapter(String chapterId, Authentication authentication) {
        // check if user is author
        Chapter chapter = chapterService.getChapterById(chapterId).orElseThrow();
        Series series = chapter.getSeries();
        // if user is not author, check if user has liked this chapter
        if (isChapterLiked(chapterId, authentication)) throw new RuntimeException("already liked");
        String username =  authentication.getName();
        Like like = new Like();
        like.setChapter(chapter);
        like.setUser((User) userManager.loadUserByUsername(username));
        likeRepository.save(like);
        chapterService.save(chapter);
    }

    public void unlikeChapter(String chapterId, Authentication authentication) {
        if (!(isChapterLiked(chapterId, authentication))) {
            throw new RuntimeException("not liked");
        }
        System.out.println("unlike");
        String userId = ((User) authentication.getPrincipal()).getId();
        likeRepository.deleteByChapter_IdAndUser_Id(chapterId, userId);
    }

    public boolean isChapterLikedBySeriesSlugAndChapterNumber(String seriesSlug, int chapterNumber, Authentication authentication) {
        // if anonymous user, return false
        if (authentication == null) return false;
        String userId = ((User) authentication.getPrincipal()).getId();
        String chapterId = chapterService.getChapterBySeriesSlugAndChapterNumber(seriesSlug, chapterNumber).getId();
        return likeRepository.existsByChapter_IdAndUser_Id(chapterId, userId);
    }
}
