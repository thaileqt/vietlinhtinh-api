package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.MarkerDTO;
import com.example.truyenchuvietsub.dto.MessageResponse;
import com.example.truyenchuvietsub.model.Chapter;
import com.example.truyenchuvietsub.model.Marker;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.ChapterRepository;
import com.example.truyenchuvietsub.repository.MarkerRepository;
import com.example.truyenchuvietsub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarkerService {
    @Autowired
    private MarkerRepository markerRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChapterRepository chapterRepository;

    public MarkerDTO createMarker(MarkerDTO markerDTO, Authentication authentication) {
        // first check if user has marker in this chapter
        User user = (User) authentication.getPrincipal();
        if (markerRepository.existsByChapter_IdAndUser_Id(markerDTO.getChapterId(), user.getId())) {
            if (markerRepository.existsByChapter_IdAndUser_IdAndParagraphIndex(markerDTO.getChapterId(), user.getId(), markerDTO.getParagraphIndex())) {
                return null;
            } else {
                deleteMarker(getUserMarkerInChapter(markerDTO.getChapterId(), authentication).getId());
            }
        }

        // get chapter by Id and return message "Chapter not found" if not found
        Chapter chapter = chapterRepository.findById(markerDTO.getChapterId()).orElseThrow();

        Marker marker = new Marker();
        marker.setChapter(chapter);
        marker.setUser(user);
        marker.setParagraphIndex(markerDTO.getParagraphIndex());
        markerRepository.save(marker);
        return MarkerDTO.from(marker);
    }

    public void deleteMarker(String markerId) {
        Marker marker = markerRepository.findById(markerId).orElseThrow();
        markerRepository.delete(marker);
    }

    public Optional<Chapter> getChapterByChapterParagraphId(String chapterParagraphId) {
        Query query = new Query(Criteria.where("content._id").is(chapterParagraphId));
        Chapter chapter = mongoTemplate.findOne(query, Chapter.class);
        return Optional.ofNullable(chapter);
    }

    // get chapter paragraph that match with user markers.chapterParagraph
    public MarkerDTO getUserMarkerInChapter(String chapterId, Authentication authentication) {
        System.out.println("Chapter Id: " + chapterId);
        User user = (User) authentication.getPrincipal();
        System.out.println("User Id: " + user.getId());
        // find marker base on chapter Id, if not exist then return null
        Optional<Marker> marker = markerRepository.findByChapter_IdAndUser_Id(chapterId, user.getId());
        if (marker.isEmpty()) {
            return null;
        }
        return MarkerDTO.from(marker.get());
    }
}
