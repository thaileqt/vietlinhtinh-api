package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.SlugGenerator;
import com.example.truyenchuvietsub.dto.series.*;
import com.example.truyenchuvietsub.dto.SeriesDTO;
import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.model.enums.EnumGenre;
import com.example.truyenchuvietsub.repository.*;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeriesStateRepository seriesStateRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private UserManager userManager;


    public int countOwnedSeries(String username) {
        User user = (User) userManager.loadUserByUsername(username);
        Query query = new Query(
                Criteria.where("author._id").is(user.getId())
        );
        return (int) mongoTemplate.count(query, Series.class);
    }

    public SeriesDetail getSeriesBySlug(String slug) {
        Series series = seriesRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Series not found"));
        MatchOperation matchOperation = Aggregation.match(Criteria.where("_id").is(new ObjectId(series.getId())));

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        // take only 10 lastest chapters
        SortOperation sortByChapterNumberDesc = Aggregation.sort(Sort.Direction.DESC, "chapters.chapterNumber");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating")
                .and("reviews").as("reviews");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation
        );

        return mongoTemplate.aggregate(aggregation, "series", SeriesDetail.class).getUniqueMappedResult();
    }

    public List<HotSeries> getTopSeriesWithHighestLikes(int seriesCount) {

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");


        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("averageRating").as("averageRating");

        SortOperation sortByTotalLikes = Aggregation.sort(Sort.Direction.DESC, "totalLikes");


        Aggregation aggregation = Aggregation.newAggregation(
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                sortByTotalLikes,
                Aggregation.limit(seriesCount) // Limit records based on size
        );
        return mongoTemplate.aggregate(aggregation, "series", HotSeries.class).getMappedResults();
    }


    public List<RecentCreatedSeries> getTopRecentCreatedSeries(int page, int size) {
        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("cover").as("cover")
                .first("createdAt").as("createdAt")
                .addToSet("chapters").as("chapters");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("cover").as("cover")
                .and("createdAt").as("createdAt")
                .and("chapters").size().as("totalChapters");

        Aggregation aggregation = Aggregation.newAggregation(
                chaptersLookupOperation,
                unwindChaptersOperation,
                groupOperation,
                projectOperation,
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );

        return mongoTemplate.aggregate(aggregation, "series", RecentCreatedSeries.class).getMappedResults();

    }

    public SeriesDTO createSeries(SeriesDTO createSeriesRequest) {
        // check if slug is already existed
        String slug = SlugGenerator.toSlug(createSeriesRequest.getTitle());
        if (seriesRepository.findBySlug(slug).isPresent()) {
            throw new RuntimeException("Slug is already existed");
        }
        // get author
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User author = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        Series series = new Series(
                createSeriesRequest.getTitle(),
                createSeriesRequest.getDescription(),
                author,
                createSeriesRequest.getCover(),
                seriesStateRepository.findByName("ONGOING").orElseThrow(() -> new RuntimeException("State not found"))
        );

        Set<String> genresStr = createSeriesRequest.getGenres();
        if (genresStr != null) {
            Set<Genre> genres = Genre.toGenreSet(genresStr, genreRepository);
            series.setGenres(genres);
        }
        seriesRepository.save(series);
        return SeriesDTO.from(series, 0, 0, 0, null);
    }


    public void deleteSeriesById(String id, Authentication authentication) {

        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
        // check if user is the author of this Series
        if (!series.getAuthor().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }
        // get all chapters of this Series
        chapterService.deleteAllBySeriesId(id);
        seriesRepository.deleteById(id);
    }

    public List<SeriesDetail> getSeriesByGenre(String genre, int page, int size) {
        // TODO: complete this method
        Genre genreObj = genreRepository.findByName(EnumGenre.valueOf(genre.toUpperCase())).orElseThrow(() -> new RuntimeException("Genre not found"));
        MatchOperation matchOperation = Aggregation.match(Criteria.where("genres").elemMatch(Criteria.where("$id").is(new ObjectId(genreObj.getId()))));

        LookupOperation chapterLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);


        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");


        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating")
                .and("reviews").as("reviews");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chapterLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );

        return mongoTemplate.aggregate(aggregation, "series", SeriesDetail.class).getMappedResults();

    }
    public List<RecentUpdatedSeries> getTopRecentUpdatedSeries(int page, int size) {

        MatchOperation matchOperation = Aggregation.match(new Criteria()); // Define your match criteria here

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("cover").as("cover")
                .first("description").as("description")
                .first("genres").as("genres")
                // get lastest chapter of this series
                .last("chapters.title").as("chapterTitle")
                .last("chapters.chapterNumber").as("chapterNumber")
                .last("chapters._id").as("chapterId")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt");

        // just take newest chapter associated with each series
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chaptersLookupOperation,
                unwindChaptersOperation,
                groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "updatedAt"),
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );

        return mongoTemplate.aggregate(aggregation, "series", RecentUpdatedSeries.class).getMappedResults();
    }

    public Series updateSeriesBySlug(String Serieslug, SeriesDTO createSeriesRequest) {
        Series series = seriesRepository.findBySlug(Serieslug).orElseThrow(() -> new RuntimeException("Series not found"));
        if (!series.getAuthor().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }
        if (createSeriesRequest.getTitle() != null) {
            series.setTitle(createSeriesRequest.getTitle());
        }
        if (createSeriesRequest.getDescription() != null) {
            series.setDescription(createSeriesRequest.getDescription());
        }
        if (createSeriesRequest.getCover() != null) {
            series.setCover(createSeriesRequest.getCover());
        }
        if (createSeriesRequest.getGenres() != null) {
            Set<String> genresStr = createSeriesRequest.getGenres();
            Set<Genre> genres = Genre.toGenreSet(genresStr, genreRepository);
            series.setGenres(genres);
        }
        seriesRepository.save(series);
        return series;
    }


    public List<SeriesDetail> search(String keyword, int page, int size) {
        if (keyword.length() < 3) {
            throw new RuntimeException("Keyword must be at least 3 characters");
        }
        String regexPattern = SlugGenerator.toSlugWithouRandom(keyword);
        MatchOperation matchOperation = Aggregation.match(Criteria.where("slug").regex(regexPattern, "i"));

        LookupOperation chapterLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating");
//                .and("reviews").as("reviews");

        return mongoTemplate.aggregate(Aggregation.newAggregation(
                matchOperation,
                chapterLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        ), "series", SeriesDetail.class).getMappedResults();
    }


    public List<UserOwnedSeriesDTO> getSeriesByUsername(String username, int page, int size) {
        // count method execution time
        String userId = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getId();
        MatchOperation matchOperation = Aggregation.match(Criteria.where("author.$id").is(new ObjectId(userId)));

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");


        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );
        List<UserOwnedSeriesDTO> result = mongoTemplate.aggregate(aggregation, "series", UserOwnedSeriesDTO.class).getMappedResults();
        return result;

    }
}
