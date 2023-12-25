package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.*;
import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.repository.ChapterRepository;
import com.example.truyenchuvietsub.repository.ChapterStateRepository;
import com.example.truyenchuvietsub.repository.CommentRepository;
import com.example.truyenchuvietsub.repository.SeriesRepository;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.image.LookupOp;
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
    private CommentRepository commentRepository;
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

    public ChapterDTO createChapter(CreateChapterRequest createChapterRequest) {
        Series series = seriesRepository.findBySlug(createChapterRequest.getSeriesSlug())
                .orElseThrow(() -> new RuntimeException("Series not found"));

        if (!series.getAuthor().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }

        int newChapterNumber = chapterRepository.findTopBySeriesOrderByChapterNumberDesc(series).map(Chapter::getChapterNumber).orElse(0) + 1;

        Chapter chapter = new Chapter(series, createChapterRequest.getTitle(), createChapterRequest.getContent(), newChapterNumber);



        ChapterState chapterState = chapterStateRepository.findByName(createChapterRequest.getChapterState().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Chapter status not found"));
        chapter.setChapterState(chapterState);

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

    public List<ChapterList> getChaptersBySeriesSlug(
            String SeriesSlug,
            int page,
            int size
    ) {
        Series series = seriesRepository.findBySlug(SeriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));

        MatchOperation match = Aggregation.match(Criteria.where("series.$id").is(new ObjectId(series.getId())));
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "chapterNumber");
        SkipOperation skip = Aggregation.skip((long) (page - 1) * size);
        LimitOperation limit = Aggregation.limit(size);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("_id").as("id")
                .first("title").as("title")
                .first("chapterNumber").as("chapterNumber")
                .first("chapterState").as("chapterState")
                .first("createdAt").as("createdAt");

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                groupOperation,
                sort,
                skip,
                limit
        );

        AggregationResults<ChapterList> results = mongoTemplate.aggregate(aggregation, "chapters", ChapterList.class);
        return results.getMappedResults();


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

        // delete all comments
        Query queryComment = new Query();
        queryComment.addCriteria(Criteria.where("chapter._id").is(chapterId));
        mongoTemplate.remove(queryComment, Comment.class);

        // delete all likes
        Query query = new Query();
        query.addCriteria(Criteria.where("chapter._id").is(chapterId));
        mongoTemplate.remove(query, Like.class);

        // delete chapter
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
        return mongoTemplate.find(query, Comment.class).stream().map(CommentDTO::from).collect(Collectors.toList());
    }

    public void save(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public List<ChapterDetail> getChapterAndAdjacentChapters(String seriesSlug, int chapterNumber) {
        Series series = seriesRepository.findBySlug(seriesSlug).orElseThrow(() -> new RuntimeException("Series not found"));

        MatchOperation match = Aggregation.match(Criteria.where("series.$id").is(new ObjectId(series.getId()))
                .andOperator(Criteria.where("chapterNumber").gte(chapterNumber - 1).lte(chapterNumber + 1)));

        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "chapterNumber");

        LookupOperation likeLookup = LookupOperation.newLookup()
                .from("likes")
                .localField("_id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation commentLookup = LookupOperation.newLookup()
                .from("comments")
                .localField("_id")
                .foreignField("chapter.$id")
                .as("comments");

        UnwindOperation unwindLikes = Aggregation.unwind("likes", true);
        UnwindOperation unwindComments = Aggregation.unwind("comments", true);

        GroupOperation groupByChapterNumber = Aggregation.group("_id")
                // get id of first chapter
                .first("_id").as("id")
                .first("chapterNumber").as("chapterNumber")
                .first("title").as("title")
                .first("content").as("content")
                .first("series").as("series")
                .first("viewCount").as("viewCount")
                .first("chapterState").as("chapterState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("series.title").as("seriesTitle")
                .first("series.cover").as("seriesCover")
                .addToSet("likes").as("likes")
                .addToSet("comments").as("comments");

        ProjectionOperation project = Aggregation.project()
                .and("id").as("id")
                .andExpression("chapterNumber").as("chapterNumber")
                .andExpression("title").as("title")
                .andExpression("content").as("content")
                .andExpression("series").as("series")
                .andExpression("chapterState").as("chapterState")
                .andExpression("createdAt").as("createdAt")
                .andExpression("updatedAt").as("updatedAt")
                .and("viewCount").as("viewCount")
                .and("likes").size().as("likeCount")
                .and("comments").as("comments");

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                likeLookup,
                unwindLikes,
                commentLookup,
                unwindComments,
                groupByChapterNumber,
                sort,
                project
                // TODO: fix this, comments is empty
        );

        AggregationResults<ChapterDetail> results = mongoTemplate.aggregate(aggregation, "chapters", ChapterDetail.class);
        return results.getMappedResults();
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

    public int countLikeByChapterId2(String chapterId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chapter._id").is(chapterId)),
                Aggregation.group().count().as("totalLikes")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "likes", Document.class);
        return results.getUniqueMappedResult() != null ? results.getUniqueMappedResult().getInteger("totalLikes") : 0;
    }
}
