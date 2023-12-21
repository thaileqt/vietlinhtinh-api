package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.ChapterDTO;
import com.example.truyenchuvietsub.dto.CommentDTO;
import com.example.truyenchuvietsub.dto.CreateChapterRequest;
import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.repository.ChapterRepository;
import com.example.truyenchuvietsub.repository.ChapterStateRepository;
import com.example.truyenchuvietsub.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterStateRepository chapterStateRepository;
    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<ChapterDTO> singleChapter(String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(() -> new RuntimeException("Chapter not found"));
        chapter.increaseViewCount();
        chapterRepository.save(chapter);
        ChapterDTO chapterDTO = ChapterDTO.from(
                chapter,
                countLikeByChapterId(chapterId),
                getCommentsByChapterId(chapterId)
        );
        return Optional.of(chapterDTO);
    }

    public List<ChapterDTO> getAllChaptersBySeriesId(String seriesId) {
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new RuntimeException("Series not found"));
        List<Chapter> chapters = chapterRepository.findAllBySeries(series);
        return chapters.stream().map(chapter -> ChapterDTO.from(chapter, countLikeByChapterId(chapter.getId()), getCommentsByChapterId(chapter.getId()))).collect(Collectors.toList());
    }

    public List<ChapterDTO> getAllChaptersBySeriesIdForNavigation(String seriesId) {
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new RuntimeException("Series not found"));
        List<Chapter> chapters = chapterRepository.findAllBySeries(series);
        return chapters.stream().map(chapter -> ChapterDTO.from(chapter, 0, null)).collect(Collectors.toList());
    }

    public Optional<Chapter> getChapterById(String chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public ChapterDTO createChapter(CreateChapterRequest createChapterRequest, Authentication authentication) {
        Series series = seriesRepository.findBySlug(createChapterRequest.getSeriesSlug())
                .orElseThrow(() -> new RuntimeException("Series not found"));

        if (!series.getAuthor().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }

        int newChapterNumber = chapterRepository.findTopBySeriesOrderByChapterNumberDesc(series).map(Chapter::getChapterNumber).orElse(0) + 1;

        // create paragraphs

        Chapter chapter = new Chapter(series, createChapterRequest.getTitle(), createChapterRequest.getContent(), newChapterNumber);

        // if cant get chaoter status then set it publish
        if (createChapterRequest.getChapterStatus() == null) {
            ChapterState chapterState = chapterStateRepository.findByName("PUBLISHED")
                    .orElseThrow(() -> new RuntimeException("Chapter status not found"));
            chapter.setChapterState(chapterState);
        } else {
            ChapterState chapterState = chapterStateRepository.findByName(createChapterRequest.getChapterStatus().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Chapter status not found"));
            chapter.setChapterState(chapterState);
        }
        chapterRepository.save(chapter);
        series.setUpdatedAt(new java.util.Date());
        seriesRepository.save(series);
        return ChapterDTO.from(chapter, 0, new ArrayList<>());
    }

    public ChapterDTO getChapterBySeriesSlugAndChapterNumber(String SeriesSlug, int chapterNumber) {
        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        Chapter chapter = chapterRepository.findChapterBySeriesAndChapterNumber(series, chapterNumber)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        chapterRepository.save(chapter);
        return ChapterDTO.from(chapter, countLikeByChapterId(chapter.getId()), getCommentsByChapterId(chapter.getId()));
    }

    public List<ChapterDTO> getChaptersBySeriesSlug(
            String SeriesSlug,
            int page,
            int size
    ) {
        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "chapterNumber"));
        Query query = new Query();

        query.addCriteria(Criteria.where("series._id").is(series.getId())).with(pageable);
        List<Chapter> chapters = mongoTemplate.find(query, Chapter.class, "chapters");
        return chapters.stream().map(chapter -> ChapterDTO.from(chapter, 0, null)).collect(Collectors.toList());
    }

    public int countChaptersBySeriesSlug(String SeriesSlug) {
        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        return chapterRepository.countChaptersBySeries(series);
    }


    public List<Chapter> getChaptersBySeries(Series series) {
        return chapterRepository.findAllBySeries(series);
    }


    public void deleteAllBySeriesId(String SeriesId) {
        Series series = seriesRepository.findById(SeriesId).orElseThrow(() -> new RuntimeException("Series not found"));
        chapterRepository.deleteAllBySeries(series);
    }

    public void deleteChapterById(String chapterId) {
        Series series = chapterRepository.findById(chapterId)
                .map(Chapter::getSeries)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        if (!series.getAuthor().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new RuntimeException("You are not the author of this Series");
        chapterRepository.deleteById(chapterId);
    }

    public Chapter updateChapterByChapterNumberAndSeriesSlug(String SeriesSlug, int chapterNumber, ChapterDTO editChapterRequest) {

        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        Optional<Chapter> oldChapter = chapterRepository.findChapterBySeriesAndChapterNumber(series, chapterNumber);
        if (oldChapter.isPresent()) {
            Chapter chapter = oldChapter.get();
            chapter.setTitle(editChapterRequest.getTitle());
            chapter.setContent(editChapterRequest.getContent());
            // set status
            ChapterState chapterState = chapterStateRepository.findByName(editChapterRequest.getChapterState().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Chapter status not found"));
            chapter.setChapterState(chapterState);
            chapter.setUpdatedAt(new java.util.Date());

            return chapterRepository.save(chapter);
        } else {
            throw new RuntimeException("Chapter id not match");
        }
    }

    public int countChaptersBySeries_Slug(String SeriesSlug) {
        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        return chapterRepository.countChaptersBySeries(series);
    }

    public int countLikeByChapterId(String chapterId) {
        // use mongo template
        Query query = new Query();
        query.addCriteria(Criteria.where("chapter._id").is(chapterId));
        return (int) mongoTemplate.count(query, Like.class);
    }

    public List<CommentDTO> getCommentsByChapterId(String chapterId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chapter._id").is(chapterId));
        // sort by createdAt
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Comment> comments = mongoTemplate.find(query, Comment.class, "comments");
        System.out.println(comments);
        return mongoTemplate.find(query, Comment.class).stream().map(CommentDTO::from).collect(Collectors.toList());
    }

    public void save(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public List<ChapterDTO> getChapterAndAdjacentChapters(String seriesSlug, int chapterNumber) {
        Series series = seriesRepository.findBySlug(seriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        Query query = new Query();
        query.addCriteria(Criteria.where("series._id").is(series.getId())
                .andOperator(Criteria.where("chapterNumber").gte(chapterNumber - 1).lte(chapterNumber + 1)));
        // add view count for chapter number

        query.with(Sort.by(Sort.Direction.ASC, "chapterNumber"));
        List<Chapter> chapters = mongoTemplate.find(query, Chapter.class, "chapters");
        for (Chapter chapter : chapters) {
            if (chapter.getChapterNumber() == chapterNumber) {
                chapter.increaseViewCount();
                chapterRepository.save(chapter);
            }
        }
        return chapters.stream().map(chapter -> ChapterDTO.from(
                chapter,
                countLikeByChapterId(chapter.getId()),
                getCommentsByChapterId(chapter.getId())))
                .collect(Collectors.toList());
    }

    public ChapterDTO getChapter(String seriesSlug, int chapterNumber) {
        Series series = seriesRepository.findBySlug(seriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));
        Chapter chapter = chapterRepository.findChapterBySeriesAndChapterNumber(series, chapterNumber)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        return ChapterDTO.from(
                chapter,
                countLikeByChapterId(chapter.getId()),
                getCommentsByChapterId(chapter.getId())
        );
    }
}
